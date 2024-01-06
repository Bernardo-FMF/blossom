package org.blossom.message.kafka.inbound;

import org.blossom.facade.KafkaResourceHandler;
import org.blossom.message.entity.User;
import org.blossom.message.mapper.UserMapper;
import org.blossom.message.repository.UserRepository;
import org.blossom.model.KafkaUserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserResourceHandler implements KafkaResourceHandler<KafkaUserResource> {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void save(KafkaUserResource resource) {
        if (!userRepository.existsById(resource.getId())) {
            userRepository.save(userMapper.mapToUser(resource));
        }
    }

    @Override
    public void update(KafkaUserResource resource) {
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
        userRepository.deleteById(resource.getId());
    }
}