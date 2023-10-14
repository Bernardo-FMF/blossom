package org.blossom.message.kafka.inbound;

import org.apache.commons.lang.NotImplementedException;
import org.blossom.facade.KafkaResourceHandler;
import org.blossom.message.dto.ChatCreationDto;
import org.blossom.message.enums.ChatType;
import org.blossom.message.exception.InvalidChatException;
import org.blossom.message.repository.UserRepository;
import org.blossom.message.service.ChatService;
import org.blossom.model.KafkaSocialFollowResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SocialFollowResourceHandler implements KafkaResourceHandler<KafkaSocialFollowResource> {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatService chatService;

    @Override
    public void save(KafkaSocialFollowResource resource) {
        if (userRepository.existsById(resource.getInitiatingUser()) && userRepository.existsById(resource.getReceivingUser()) && resource.isMutualFollow()) {
            try {
                ChatCreationDto chatCreation = new ChatCreationDto();
                chatCreation.setInitialParticipants(List.of(resource.getInitiatingUser(), resource.getReceivingUser()));

                chatService.createChat(chatCreation, resource.getInitiatingUser(), ChatType.PRIVATE);
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
