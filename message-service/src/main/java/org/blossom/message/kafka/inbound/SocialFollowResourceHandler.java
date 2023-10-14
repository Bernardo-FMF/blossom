package org.blossom.message.kafka.inbound;

import org.apache.commons.lang.NotImplementedException;
import org.blossom.facade.KafkaResourceHandler;
import org.blossom.message.dto.ChatCreationDto;
import org.blossom.message.dto.ChatDto;
import org.blossom.message.entity.Chat;
import org.blossom.message.enums.BroadcastType;
import org.blossom.message.enums.ChatType;
import org.blossom.message.exception.InvalidChatException;
import org.blossom.message.repository.ChatRepository;
import org.blossom.message.repository.UserRepository;
import org.blossom.message.service.BroadcastService;
import org.blossom.message.service.ChatService;
import org.blossom.model.KafkaSocialFollowResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SocialFollowResourceHandler implements KafkaResourceHandler<KafkaSocialFollowResource> {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ChatService chatService;

    @Autowired
    private BroadcastService broadcastService;

    @Override
    public void save(KafkaSocialFollowResource resource) {
        if (userRepository.existsById(resource.getInitiatingUser()) && userRepository.existsById(resource.getReceivingUser()) && resource.isMutualFollow()) {
            try {
                ChatCreationDto chatCreation = new ChatCreationDto();
                chatCreation.setInitialParticipants(List.of(resource.getInitiatingUser(), resource.getReceivingUser()));

                ChatDto chatDto = chatService.createChat(chatCreation, resource.getInitiatingUser(), ChatType.PRIVATE);

                Optional<Chat> optionalChat = chatRepository.findById(chatDto.getId());
                optionalChat.ifPresent(chat -> broadcastService.broadcastChat(chat, BroadcastType.CHAT_CREATED));
            } catch (InvalidChatException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void update(KafkaSocialFollowResource resource) {
        throw new NotImplementedException("Social follow updates are not available");
    }

    @Override
    public void delete(KafkaSocialFollowResource resource) {
        throw new NotImplementedException("Social follow updates are not processed");
    }
}
