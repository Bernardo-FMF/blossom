package org.blossom.message.service;

import jakarta.transaction.Transactional;
import org.blossom.message.dto.*;
import org.blossom.message.entity.Chat;
import org.blossom.message.entity.User;
import org.blossom.message.enums.BroadcastType;
import org.blossom.message.exception.ChatNotFoundException;
import org.blossom.message.exception.IllegalChatOperationException;
import org.blossom.message.exception.InvalidChatException;
import org.blossom.message.exception.UserNotFoundException;
import org.blossom.message.factory.impl.ChatFactory;
import org.blossom.message.mapper.impl.ChatDtoMapper;
import org.blossom.message.mapper.impl.GenericDtoMapper;
import org.blossom.message.mapper.impl.UserChatsDtoMapper;
import org.blossom.message.repository.ChatRepository;
import org.blossom.message.repository.MessageRepository;
import org.blossom.message.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private BroadcastService broadcastService;

    @Autowired
    private ChatDtoMapper chatDtoMapper;

    @Autowired
    private GenericDtoMapper genericDtoMapper;

    @Autowired
    private ChatFactory chatFactory;

    @Autowired
    private UserChatsDtoMapper userChatsDtoMapper;

    public Set<User> getUsersInChat(int chatId) throws ChatNotFoundException {
        Optional<Chat> optionalChat = chatRepository.findById(chatId);
        if (optionalChat.isEmpty()) {
            throw new ChatNotFoundException("Chat does not exist");
        }
        return optionalChat.get().getParticipants();
    }

    public ChatDto createChat(ChatCreationDto chatCreation, int userId) throws InvalidChatException {
        if (chatCreation.getInitialParticipants().isEmpty() ||
                (chatCreation.getInitialParticipants().size() == 1 && chatCreation.getInitialParticipants().contains(userId))) {
            throw new InvalidChatException("Chat participants are not valid");
        }

        List<Integer> userIds = new ArrayList<>(chatCreation.getInitialParticipants());
        if (!userIds.contains(userId)) {
            userIds.add(userId);
        }

        List<Integer> uniqueUserIds = userIds.stream().distinct().toList();

        Map<Integer, User> participants = userRepository.findAllById(uniqueUserIds).stream().collect(Collectors.toMap(User::getId, localUser -> localUser));

        if (participants.size() < uniqueUserIds.size()) {
            throw new InvalidChatException("Not all users exist");
        }

        Chat chat = chatFactory.buildEntity(new HashSet<>(participants.values()), participants.get(userId), chatCreation);

        Chat newChat = chatRepository.save(chat);

        broadcastService.broadcastChat(newChat, BroadcastType.CHAT_CREATED, newChat.getParticipants());

        return chatDtoMapper.toDto(newChat);
    }

    public UserChatsDto getUserChats(SearchParametersDto searchParameters, int userId) {
        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit(), Sort.by(Sort.Direction.ASC, "lastUpdate")) : Pageable.unpaged();

        Page<Chat> userChats = chatRepository.findByUserId(userId, page);

        PaginationInfoDto paginationInfo = new PaginationInfoDto(userChats.getTotalPages(), userChats.getNumber(), userChats.getTotalElements(), !userChats.hasNext());
        return userChatsDtoMapper.toPaginatedDto(userChats.getContent(), paginationInfo);
    }

    public GenericResponseDto addToChat(int chatId, int userId, int participantId) throws ChatNotFoundException, IllegalChatOperationException, UserNotFoundException {
        Optional<Chat> optionalChat = chatRepository.findById(chatId);
        if (optionalChat.isEmpty()) {
            throw new ChatNotFoundException("Chat does not exist");
        }

        Chat chat = optionalChat.get();

        if (!chat.isGroup()) {
            throw new IllegalChatOperationException("Cannot add participants to a chat that is not a group");
        }

        if (chat.getParticipants().stream().noneMatch(participant -> participant.getId() == participantId)) {
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
        chat.setLastUpdate(Instant.now());

        chatRepository.save(chat);

        return genericDtoMapper.toDto("User added to chat with success", chatId, null);
    }

    @Transactional
    public GenericResponseDto leaveChat(int chatId, int userId) throws ChatNotFoundException, IllegalChatOperationException, UserNotFoundException {
        Optional<Chat> optionalChat = chatRepository.findById(chatId);
        if (optionalChat.isEmpty()) {
            throw new ChatNotFoundException("Chat does not exist");
        }

        Chat chat = optionalChat.get();

        if (!chat.isGroup()) {
            throw new IllegalChatOperationException("Cannot leave a chat that is not a group");
        }

        if (chat.getParticipants().stream().noneMatch(participant -> participant.getId() == userId)) {
            throw new IllegalChatOperationException("User is not in the chat");
        }

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }

        User user = optionalUser.get();

        Set<User> participants = new HashSet<>(chat.getParticipants());
        chat.removeFromChat(user);

        if (chat.getParticipants().isEmpty()) {
            messageRepository.deleteByChatId(chatId);
            chatRepository.deleteById(chatId);
            broadcastService.broadcastChat(chat, BroadcastType.CHAT_DELETED, participants);

            return genericDtoMapper.toDto("Chat was deleted due to no participants", chatId, null);
        } else {
            if (chat.getOwner() == null) {
                chat.setNewOwner(chat.getParticipants().stream().findFirst().get());
            }
            chat.setLastUpdate(Instant.now());

            chatRepository.save(chat);

            broadcastService.broadcastChat(chat, BroadcastType.CHAT_UPDATED, chat.getParticipants());
            broadcastService.broadcastChat(chat, BroadcastType.CHAT_DELETED, Set.of(user));

            return genericDtoMapper.toDto("User removed from chat with success", chatId, null);
        }
    }

    @Transactional
    public void decoupleUserFromChat(Chat chat, User user) throws ChatNotFoundException, IllegalChatOperationException, UserNotFoundException {
        if (chat.getParticipants().stream().noneMatch(participant -> participant.getId() == user.getId())) {
            throw new IllegalChatOperationException("User is not in the chat");
        }

        Set<User> participants = new HashSet<>(chat.getParticipants());
        chat.removeFromChat(user);

        if (chat.getParticipants().isEmpty()) {
            messageRepository.deleteByChatId(chat.getId());
            chatRepository.deleteById(chat.getId());
            broadcastService.broadcastChat(chat, BroadcastType.CHAT_DELETED, participants);
        } else {
            messageRepository.decoupleUserFromChat(chat.getId(), user.getId());

            if (chat.getOwner() == null) {
                chat.setNewOwner(chat.getParticipants().stream().findFirst().orElse(null));
            }
            chat.setLastUpdate(Instant.now());

            chatRepository.save(chat);

            broadcastService.broadcastChat(chat, BroadcastType.CHAT_UPDATED, chat.getParticipants());
            broadcastService.broadcastChat(chat, BroadcastType.CHAT_DELETED, Set.of(user));
        }
    }

    public GenericResponseDto removeFromChat(int chatId, int userId, int ownerId) throws ChatNotFoundException, IllegalChatOperationException, UserNotFoundException {
        Optional<Chat> optionalChat = chatRepository.findById(chatId);
        if (optionalChat.isEmpty()) {
            throw new ChatNotFoundException("Chat does not exist");
        }

        Chat chat = optionalChat.get();

        if (!chat.isGroup()) {
            throw new IllegalChatOperationException("Cannot remove participants from a chat that is not a group");
        }

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
        chat.setLastUpdate(Instant.now());

        chatRepository.save(chat);

        return genericDtoMapper.toDto("User removed from chat with success", chatId, null);
    }

    @Transactional
    public GenericResponseDto deleteChat(Integer chatId, int userId) throws ChatNotFoundException, IllegalChatOperationException {
        Optional<Chat> optionalChat = chatRepository.findById(chatId);
        if (optionalChat.isEmpty()) {
            throw new ChatNotFoundException("Chat does not exist");
        }

        Chat chat = optionalChat.get();

        if (!chat.isGroup()) {
            throw new IllegalChatOperationException("Cannot remove participants from a chat that is not a group");
        }

        if (chat.getOwner().getId() != userId) {
            throw new IllegalChatOperationException("Authenticated user is not the owner of the chat");
        }

        messageRepository.deleteByChatId(chat.getId());
        chatRepository.deleteById(chat.getId());
        broadcastService.broadcastChat(chat, BroadcastType.CHAT_DELETED, chat.getParticipants());

        return genericDtoMapper.toDto("Chat deleted with success", chatId, null);
    }
}
