package org.blossom.feed.kafka;

import org.apache.commons.lang.NotImplementedException;
import org.blossom.facade.KafkaResourceHandler;
import org.blossom.feed.entity.LocalUser;
import org.blossom.feed.mapper.LocalUserMapper;
import org.blossom.feed.repository.LocalUserPostsRepository;
import org.blossom.feed.repository.LocalUserRepository;
import org.blossom.model.KafkaUserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserResourceHandler implements KafkaResourceHandler<KafkaUserResource> {
    @Autowired
    private LocalUserRepository localUserRepository;

    @Autowired
    private LocalUserPostsRepository localUserPostsRepository;

    @Autowired
    private LocalUserMapper localUserMapper;

    @Override
    public void save(KafkaUserResource resource) {
        if (!localUserRepository.existsById(resource.getId())) {
            localUserRepository.save(localUserMapper.mapToLocalUser(resource));
            localUserPostsRepository.save(localUserMapper.mapToLocalUserPosts(resource.getId()));
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