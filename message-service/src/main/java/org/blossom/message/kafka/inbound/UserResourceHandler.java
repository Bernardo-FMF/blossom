package org.blossom.message.kafka.inbound;

import lombok.extern.log4j.Log4j2;
import org.blossom.facade.KafkaResourceHandler;
import org.blossom.message.entity.Chat;
import org.blossom.message.entity.User;
import org.blossom.message.exception.ChatNotFoundException;
import org.blossom.message.exception.IllegalChatOperationException;
import org.blossom.message.exception.UserNotFoundException;
import org.blossom.message.factory.impl.UserFactory;
import org.blossom.message.repository.ChatRepository;
import org.blossom.message.repository.UserRepository;
import org.blossom.message.service.ChatService;
import org.blossom.model.KafkaUserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class UserResourceHandler implements KafkaResourceHandler<KafkaUserResource> {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserFactory userFactory;

    @Autowired
    private ChatService chatService;

    @Override
    public void save(KafkaUserResource resource) {
        log.info("processing save message of type user: {}", resource);

        if (!userRepository.existsById(resource.getId())) {
            userRepository.save(userFactory.buildEntity(resource));
        }
    }

    @Override
    public void update(KafkaUserResource resource) {
        log.info("processing update message of type user: {}", resource);

        Optional<User> optionalUser = userRepository.findById(resource.getId());
        if (optionalUser.isEmpty()) {
            save(resource);
            return;
        }

        User localUser = optionalUser.get();
        localUser.setImageUrl(resource.getImageUrl());

        userRepository.save(localUser);
    }

    @Override
    public void delete(KafkaUserResource resource) {
        log.info("processing delete message of type user: {}", resource);

        Optional<User> optionalUser = userRepository.findById(resource.getId());
        if (optionalUser.isEmpty()) {
            return;
        }

        User user = optionalUser.get();

        List<Chat> userChats = chatRepository.findByUserId(resource.getId());

        userChats.forEach(chat -> {
            try {
                chatService.decoupleUserFromChat(chat, user);
            } catch (ChatNotFoundException | UserNotFoundException | IllegalChatOperationException e) {
                log.error("error removing user from chat", e);
            }
        });

        userRepository.deleteById(resource.getId());
    }
}