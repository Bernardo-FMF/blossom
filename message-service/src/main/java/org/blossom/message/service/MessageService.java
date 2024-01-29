package org.blossom.message.service;

import org.blossom.message.dto.*;
import org.blossom.message.entity.Chat;
import org.blossom.message.entity.Message;
import org.blossom.message.entity.User;
import org.blossom.message.exception.ChatNotFoundException;
import org.blossom.message.exception.IllegalMessageOperationException;
import org.blossom.message.exception.MessageNotFoundException;
import org.blossom.message.exception.UserNotFoundException;
import org.blossom.message.factory.impl.MessageFactory;
import org.blossom.message.mapper.impl.MessageDtoMapper;
import org.blossom.message.mapper.impl.ChatMessagesDtoMapper;
import org.blossom.message.repository.ChatRepository;
import org.blossom.message.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class MessageService {
    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageDtoMapper messageDtoMapper;

    @Autowired
    private MessageFactory messageFactory;

    @Autowired
    private ChatMessagesDtoMapper chatMessagesDtoMapper;

    public Message createMessage(PublishMessageDto chatMessage, int chatId, int userId) throws UserNotFoundException, ChatNotFoundException {
        Optional<Chat> optionalChat = chatRepository.findById(chatId);
        if (optionalChat.isEmpty()) {
            throw new ChatNotFoundException("Chat does not exist");
        }

        Chat chat = optionalChat.get();

        Optional<User> optionalUser = chat.getParticipants().stream().filter(participant -> participant.getId() == userId).findFirst();
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User is not a chat participant");
        }

        Message message = messageFactory.buildEntity(chat, optionalUser.get(), chatMessage);

        return messageRepository.save(message);
    }

    public Message deleteMessage(DeleteMessageDto deleteMessage, int userId) throws IllegalMessageOperationException, MessageNotFoundException {
        Optional<Message> optionalMessage = messageRepository.findById(deleteMessage.getId());
        if (optionalMessage.isEmpty()) {
            throw new MessageNotFoundException("Message does not exist");
        }

        Message message = optionalMessage.get();
        if (message.getSender().getId() != userId) {
            throw new IllegalMessageOperationException("Message is not associated with the user");
        }

        message.setUpdatedAt(Instant.now());
        message.setDeleted(true);
        message.setContent(null);

        return messageRepository.save(message);
    }

    public Message updateMessage(UpdateMessageDto updateMessage, int userId) throws IllegalMessageOperationException, MessageNotFoundException {
        Optional<Message> optionalMessage = messageRepository.findById(updateMessage.getId());
        if (optionalMessage.isEmpty()) {
            throw new MessageNotFoundException("Message does not exist");
        }

        Message message = optionalMessage.get();
        if (message.getSender().getId() != userId) {
            throw new IllegalMessageOperationException("Message is not associated with the user");
        }

        message.setContent(updateMessage.getNewContent());
        message.setUpdatedAt(Instant.now());

        return messageRepository.save(message);
    }

    public ChatMessagesDto getChatMessages(Integer chatId, SearchParametersDto searchParameters, int userId) throws ChatNotFoundException, UserNotFoundException {
        Optional<Chat> optionalChat = chatRepository.findById(chatId);
        if (optionalChat.isEmpty()) {
            throw new ChatNotFoundException("Chat does not exist");
        }

        Chat chat = optionalChat.get();
        if (chat.getParticipants().stream().noneMatch(participant -> participant.getId() == userId)) {
            throw new UserNotFoundException("User is not a chat participant");
        }

        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit(), Sort.by(Sort.Direction.DESC, "createdAt")) : Pageable.unpaged();

        Page<Message> messages = messageRepository.findByChatId(chatId, page);

        PaginationInfoDto paginationInfo = new PaginationInfoDto(messages.getTotalPages(), messages.getNumber(), messages.getTotalElements(), !messages.hasNext());
        return chatMessagesDtoMapper.toPaginatedDto(messages.getContent(), paginationInfo);
    }
}
