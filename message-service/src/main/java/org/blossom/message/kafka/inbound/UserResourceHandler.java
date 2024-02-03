package org.blossom.message.kafka.inbound;

import lombok.extern.log4j.Log4j2;
import org.blossom.facade.KafkaResourceHandler;
import org.blossom.message.entity.User;
import org.blossom.message.factory.impl.UserFactory;
import org.blossom.message.repository.UserRepository;
import org.blossom.model.KafkaUserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Log4j2
public class UserResourceHandler implements KafkaResourceHandler<KafkaUserResource> {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFactory userFactory;

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

        //TODO: delete chat and message if necessary
        userRepository.deleteById(resource.getId());
    }
}