package org.blossom.activity.kafka;

import org.apache.commons.lang.NotImplementedException;
import org.blossom.activity.entity.LocalUser;
import org.blossom.facade.KafkaResourceHandler;
import org.blossom.activity.mapper.LocalUserMapper;
import org.blossom.model.KafkaUserResource;
import org.blossom.activity.repository.LocalUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserResourceHandler implements KafkaResourceHandler<KafkaUserResource> {
    @Autowired
    private LocalUserRepository localUserRepository;

    @Autowired
    private LocalUserMapper localUserMapper;

    @Override
    public void save(KafkaUserResource resource) {
        if (!localUserRepository.existsById(resource.getId())) {
            localUserRepository.save(localUserMapper.mapToLocalUser(resource));
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
        throw new NotImplementedException("User deletes are not available");
    }
}