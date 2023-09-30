package org.blossom.feed.kafka;

import org.apache.commons.lang.NotImplementedException;
import org.blossom.facade.KafkaResourceHandler;
import org.blossom.feed.entity.LocalUserPosts;
import org.blossom.feed.grpc.service.GrpcClientSocialService;
import org.blossom.feed.mapper.FeedEntryMapper;
import org.blossom.feed.mapper.LocalPostMapper;
import org.blossom.feed.mapper.LocalUserMapper;
import org.blossom.feed.repository.FeedEntryRepository;
import org.blossom.feed.repository.LocalPostRepository;
import org.blossom.feed.repository.LocalUserPostsRepository;
import org.blossom.model.KafkaPostResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostResourceHandler implements KafkaResourceHandler<KafkaPostResource> {
    @Autowired
    private LocalPostRepository localPostRepository;

    @Autowired
    private FeedEntryRepository feedEntryRepository;

    @Autowired
    private LocalUserPostsRepository localUserPostsRepository;

    @Autowired
    private LocalPostMapper localPostMapper;

    @Autowired
    private FeedEntryMapper feedEntryMapper;

    @Autowired
    private LocalUserMapper localUserMapper;

    @Autowired
    private GrpcClientSocialService grpcClientSocialService;

    @Override
    public void save(KafkaPostResource resource) {
        if (!localPostRepository.existsById(resource.getId())) {
            localPostRepository.save(localPostMapper.mapToLocalPost(resource));

            Optional<LocalUserPosts> optionalUser = localUserPostsRepository.findById(resource.getUserId());
            LocalUserPosts localUserPosts = optionalUser.orElseGet(() -> localUserMapper.mapToLocalUserPosts(resource.getUserId()));
            localUserPosts.addPost(resource.getId());
            localUserPostsRepository.save(localUserPosts);

            List<Integer> userFollowers = grpcClientSocialService.getUserFollowers(resource.getUserId());
            feedEntryRepository.saveAll(userFollowers.stream().map(userId -> feedEntryMapper.mapToFeedEntry(resource.getId(), userId)).collect(Collectors.toList()));
        }
    }

    @Override
    public void update(KafkaPostResource resource) {
        throw new NotImplementedException("Post updates are not available");
    }

    @Override
    public void delete(KafkaPostResource resource) {
        localPostRepository.deleteById(resource.getId());
        feedEntryRepository.deleteByPostId(resource.getId());
    }
}
