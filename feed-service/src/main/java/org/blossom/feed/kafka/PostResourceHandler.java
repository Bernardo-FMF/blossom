package org.blossom.feed.kafka;

import lombok.extern.log4j.Log4j2;
import org.blossom.facade.KafkaResourceHandler;
import org.blossom.feed.entity.FeedEntry;
import org.blossom.feed.entity.LocalPostByUser;
import org.blossom.feed.factory.impl.FeedEntryFactory;
import org.blossom.feed.factory.impl.LocalPostByUserFactory;
import org.blossom.feed.grpc.service.GrpcClientSocialService;
import org.blossom.feed.repository.FeedEntryRepository;
import org.blossom.feed.repository.LocalPostByUserRepository;
import org.blossom.feed.repository.LocalUserPostCountRepository;
import org.blossom.model.KafkaPostResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class PostResourceHandler implements KafkaResourceHandler<KafkaPostResource> {
    @Autowired
    private FeedEntryRepository feedEntryRepository;

    @Autowired
    private LocalUserPostCountRepository localUserPostCountRepository;

    @Autowired
    private LocalPostByUserRepository localPostByUserRepository;

    @Autowired
    private FeedEntryFactory feedEntryFactory;

    @Autowired
    private LocalPostByUserFactory localPostByUserFactory;

    @Autowired
    private GrpcClientSocialService grpcClientSocialService;

    @Override
    public void save(KafkaPostResource resource) {
        log.info("processing save message of type post: {}", resource);

        localUserPostCountRepository.incrementCount(resource.getUserId());

        localPostByUserRepository.save(localPostByUserFactory.buildEntity(resource));

        List<Integer> userFollowers = grpcClientSocialService.getUserFollowers(resource.getUserId());
        feedEntryRepository.saveAll(userFollowers.stream().map(userId -> feedEntryFactory.buildEntity(resource, userId)).collect(Collectors.toList()));
    }

    @Override
    public void update(KafkaPostResource resource) {
        log.info("discarding update message of type post: {}", resource);
    }

    @Override
    public void delete(KafkaPostResource resource) {
        log.info("processing delete message of type post: {}", resource);

        List<FeedEntry> feedEntriesToDelete = feedEntryRepository.findByPostIdIn(List.of(resource.getId()));
        feedEntryRepository.deleteAll(feedEntriesToDelete);

        List<LocalPostByUser> localPostByUsersToDelete = localPostByUserRepository.findByPostIdIn(List.of(resource.getId()));
        localPostByUserRepository.deleteAll(localPostByUsersToDelete);

        localUserPostCountRepository.decrementCount(resource.getUserId());
    }
}
