package org.blossom.feed.kafka;

import org.blossom.facade.KafkaResourceHandler;
import org.blossom.feed.entity.FeedEntry;
import org.blossom.feed.entity.LocalPostByUser;
import org.blossom.feed.entity.LocalUser;
import org.blossom.feed.mapper.LocalUserMapper;
import org.blossom.feed.repository.FeedEntryRepository;
import org.blossom.feed.repository.LocalPostByUserRepository;
import org.blossom.feed.repository.LocalUserPostCountRepository;
import org.blossom.feed.repository.LocalUserRepository;
import org.blossom.model.KafkaUserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserResourceHandler implements KafkaResourceHandler<KafkaUserResource> {
    @Autowired
    private LocalUserRepository localUserRepository;

    @Autowired
    private LocalUserPostCountRepository localUserPostCountRepository;

    @Autowired
    private LocalUserMapper localUserMapper;

    @Autowired
    private LocalPostByUserRepository localPostByUserRepository;

    @Autowired
    private FeedEntryRepository feedEntryRepository;

    @Override
    public void save(KafkaUserResource resource) {
        if (!localUserRepository.existsById(resource.getId())) {
            localUserRepository.save(localUserMapper.mapToLocalUser(resource));
            localUserPostCountRepository.createCount(resource.getId());
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
        localUserPostCountRepository.deleteById(resource.getId());

        List<LocalPostByUser> postsToDelete = localPostByUserRepository.findAllById(List.of(resource.getId()));
        localPostByUserRepository.deleteById(resource.getId());

        List<FeedEntry> feedEntriesToDelete = feedEntryRepository.findByPostIdIn(postsToDelete.stream().map(LocalPostByUser::getPostId).toList());
        feedEntryRepository.deleteAll(feedEntriesToDelete);
    }
}