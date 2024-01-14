package org.blossom.activity.kafka;

import org.blossom.activity.entity.LocalUser;
import org.blossom.activity.factory.impl.LocalUserFactory;
import org.blossom.activity.repository.LocalUserRepository;
import org.blossom.facade.KafkaResourceHandler;
import org.blossom.model.KafkaUserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserResourceHandler implements KafkaResourceHandler<KafkaUserResource> {
    @Autowired
    private LocalUserRepository localUserRepository;

    @Autowired
    private LocalUserFactory localUserFactory;

    @Override
    public void save(KafkaUserResource resource) {
        if (!localUserRepository.existsById(resource.getId())) {
            localUserRepository.save(localUserFactory.buildEntity(resource));
        }
    }

    @Override
    public void update(KafkaUserResource resource) {
        Optional<LocalUser> optionalLocalUser = localUserRepository.findById(resource.getId());
        if (optionalLocalUser.isEmpty()) {
            save(resource);
            return;
        }

        LocalUser localUser = optionalLocalUser.get();
        localUser.setImageUrl(resource.getImageUrl());

        localUserRepository.save(localUser);
    }

    @Override
    public void delete(KafkaUserResource resource) {
        localUserRepository.deleteById(resource.getId());
    }
}