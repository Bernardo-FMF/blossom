package org.blossom.message.service;

import org.blossom.message.dto.ChatCreationDto;
import org.blossom.message.dto.ChatDto;
import org.blossom.message.dto.SearchParametersDto;
import org.blossom.message.dto.UserChatsDto;
import org.blossom.message.entity.Chat;
import org.blossom.message.entity.User;
import org.blossom.message.enums.BroadcastType;
import org.blossom.message.exception.ChatNotFoundException;
import org.blossom.message.exception.IllegalChatOperationException;
import org.blossom.message.exception.InvalidChatException;
import org.blossom.message.exception.UserNotFoundException;
import org.blossom.message.mapper.ChatDtoMapper;
import org.blossom.message.repository.ChatRepository;
import org.blossom.message.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BroadcastService broadcastService;

    @Autowired
    private ChatDtoMapper chatDtoMapper;

    public Set<User> getUsersInChat(int chatId) throws ChatNotFoundException {
        Optional<Chat> optionalChat = chatRepository.findById(chatId);
        if (optionalChat.isEmpty()) {
            throw new ChatNotFoundException("Chat does not exist");
        }
        return optionalChat.get().getParticipants();
    }

    public ChatDto createChat(ChatCreationDto chatCreation, int userId) throws InvalidChatException {
        if (chatCreation.getInitialParticipants().isEmpty() || chatCreation.getInitialParticipants().contains(userId)) {
            throw new InvalidChatException("Chat participants are not valid");
        }

        List<Integer> userIds = new ArrayList<>(chatCreation.getInitialParticipants());
        userIds.add(userId);

        Map<Integer, User> participants = userRepository.findAllById(userIds).stream().collect(Collectors.toMap(User::getId, localUser -> localUser));

        if (participants.size() < chatCreation.getInitialParticipants().size() + 1) {
            throw new InvalidChatException("Not all users exist");
        }

        Chat chat = Chat.builder()
                .owner(participants.get(userId))
                .participants(new HashSet<>(participants.values()))
                .name(chatCreation.getName())
                .build();

        Chat newChat = chatRepository.save(chat);

        broadcastService.broadcastChat(newChat, BroadcastType.CHAT_CREATED);

        return chatDtoMapper.mapToChatDto(newChat);
    }

    public UserChatsDto getUserChats(SearchParametersDto searchParameters, int userId) {
        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit(), Sort.by(Sort.Direction.DESC, "lastUpdate")) : Pageable.unpaged();

        Page<Chat> userChats = chatRepository.findByUserId(userId, page);

        return UserChatsDto.builder()
                .chats(userChats.get().map(userChat -> chatDtoMapper.mapToChatDto(userChat)).collect(Collectors.toList()))
                .currentPage(userChats.getNumber())
                .totalPages(userChats.getTotalPages())
                .totalElements(userChats.getTotalElements())
                .eof(!userChats.hasNext())
                .build();
    }

    public String addToChat(int chatId, int userId, int ownerId) throws ChatNotFoundException, IllegalChatOperationException, UserNotFoundException {
        Optional<Chat> optionalChat = chatRepository.findById(chatId);
        if (optionalChat.isEmpty()) {
            throw new ChatNotFoundException("Chat does not exist");
        }

        Chat chat = optionalChat.get();

        if (chat.getOwner().getId() != ownerId) {
            throw new IllegalChatOperationException("Authenticated user is not the owner of the chat");
        }

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }

        if (chat.getParticipants().stream().anyMatch(participant -> participant.getId() == userId)) {
            throw new IllegalChatOperationException("User is already in the chat");
        }

        User user = optionalUser.get();
        chat.addToChat(user);

        return "User added to chat with success";
    }

    public String leaveChat(int chatId, int userId) throws ChatNotFoundException, IllegalChatOperationException, UserNotFoundException {
        Optional<Chat> optionalChat = chatRepository.findById(chatId);
        if (optionalChat.isEmpty()) {
            throw new ChatNotFoundException("Chat does not exist");
        }

        Chat chat = optionalChat.get();

        if (chat.getParticipants().stream().noneMatch(participant -> participant.getId() == userId)) {
            throw new IllegalChatOperationException("User is not in the chat");
        }

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }

        User user = optionalUser.get();

        chat.removeFromChat(user);

        if (chat.getParticipants().isEmpty()) {
            chatRepository.deleteById(chatId);
            return "Chat was deleted due to no participants";
        } else {
            chat.setNewOwner(chat.getParticipants().stream().findAny().get());
            chatRepository.save(chat);

            return "User removed from chat with success";
        }
    }

    public String removeFromChat(int chatId, int userId, int ownerId) throws ChatNotFoundException, IllegalChatOperationException, UserNotFoundException {
        Optional<Chat> optionalChat = chatRepository.findById(chatId);
        if (optionalChat.isEmpty()) {
            throw new ChatNotFoundException("Chat does not exist");
        }

        Chat chat = optionalChat.get();

        if (chat.getOwner().getId() != ownerId) {
            throw new IllegalChatOperationException("Authenticated user is not the owner of the chat");
        }

        if (userId == ownerId) {
            throw new IllegalChatOperationException("Cannot remove self from chat");
        }

        if (chat.getParticipants().stream().noneMatch(participant -> participant.getId() == userId)) {
            throw new IllegalChatOperationException("User is not in the chat");
        }

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }

        User user = optionalUser.get();

        chat.removeFromChat(user);
        chatRepository.save(chat);

        return "User removed from chat with success";
    }
}
