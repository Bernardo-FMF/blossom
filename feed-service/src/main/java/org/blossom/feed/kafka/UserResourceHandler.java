package org.blossom.feed.kafka;

import org.blossom.facade.KafkaResourceHandler;
import org.blossom.feed.entity.FeedEntry;
import org.blossom.feed.entity.LocalPostByUser;
import org.blossom.feed.entity.LocalUser;
import org.blossom.feed.factory.impl.LocalUserFactory;
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
    private LocalPostByUserRepository localPostByUserRepository;

    @Autowired
    private FeedEntryRepository feedEntryRepository;

    @Autowired
    private LocalUserFactory localUserFactory;

    @Override
    public void save(KafkaUserResource resource) {
        if (!localUserRepository.existsById(resource.getId())) {
            localUserRepository.save(localUserFactory.buildEntity(resource));
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

        List<LocalPostByUser> postsToDelete = localPostByUserRepository.findByKeyUserIdIn(List.of(resource.getId()));
        localPostByUserRepository.deleteAll(postsToDelete);

        List<FeedEntry> feedEntriesToDelete = feedEntryRepository.findByPostIdIn(postsToDelete.stream().map(LocalPostByUser::getPostId).toList());
        feedEntryRepository.deleteAll(feedEntriesToDelete);
    }
}