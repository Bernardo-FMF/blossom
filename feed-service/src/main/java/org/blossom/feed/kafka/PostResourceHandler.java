package org.blossom.feed.kafka;

import org.apache.commons.lang.NotImplementedException;
import org.blossom.facade.KafkaResourceHandler;
import org.blossom.feed.entity.FeedEntry;
import org.blossom.feed.entity.LocalPostByUser;
import org.blossom.feed.grpc.service.GrpcClientSocialService;
import org.blossom.feed.mapper.FeedEntryMapper;
import org.blossom.feed.mapper.LocalPostByUserMapper;
import org.blossom.feed.mapper.LocalUserMapper;
import org.blossom.feed.repository.FeedEntryRepository;
import org.blossom.feed.repository.LocalPostByUserRepository;
import org.blossom.feed.repository.LocalUserPostCountRepository;
import org.blossom.model.KafkaPostResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostResourceHandler implements KafkaResourceHandler<KafkaPostResource> {
    @Autowired
    private FeedEntryRepository feedEntryRepository;

    @Autowired
    private LocalUserPostCountRepository localUserPostCountRepository;

    @Autowired
    private LocalPostByUserRepository localPostByUserRepository;

    @Autowired
    private FeedEntryMapper feedEntryMapper;

    @Autowired
    private LocalUserMapper localUserMapper;

    @Autowired
    private LocalPostByUserMapper localPostByUserMapper;

    @Autowired
    private GrpcClientSocialService grpcClientSocialService;

    @Override
    public void save(KafkaPostResource resource) {
        localUserPostCountRepository.incrementCount(resource.getUserId());

        localPostByUserRepository.save(localPostByUserMapper.mapToLocalPostUsers(resource));

        List<Integer> userFollowers = grpcClientSocialService.getUserFollowers(resource.getUserId());
        feedEntryRepository.saveAll(userFollowers.stream().map(userId -> feedEntryMapper.mapToFeedEntry(resource, userId)).collect(Collectors.toList()));
    }

    @Override
    public void update(KafkaPostResource resource) {
        throw new NotImplementedException("Post updates are not available");
    }

    @Override
    public void delete(KafkaPostResource resource) {
        List<FeedEntry> feedEntriesToDelete = feedEntryRepository.findByPostIdIn(List.of(resource.getId()));
        feedEntryRepository.deleteAll(feedEntriesToDelete);

        List<LocalPostByUser> localPostByUsersToDelete = localPostByUserRepository.findByPostIdIn(List.of(resource.getId()));
        localPostByUserRepository.deleteAll(localPostByUsersToDelete);

        localUserPostCountRepository.decrementCount(resource.getUserId());
    }
}
