package org.blossom.feed.kafka;

import org.apache.commons.lang.NotImplementedException;
import org.blossom.facade.KafkaResourceHandler;
import org.blossom.feed.entity.InvertedFeedEntry;
import org.blossom.feed.entity.LocalUserPostCount;
import org.blossom.feed.grpc.service.GrpcClientSocialService;
import org.blossom.feed.mapper.FeedEntryMapper;
import org.blossom.feed.mapper.LocalPostByUserMapper;
import org.blossom.feed.mapper.LocalPostMapper;
import org.blossom.feed.mapper.LocalUserMapper;
import org.blossom.feed.repository.*;
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
    private InvertedFeedEntryRepository invertedFeedEntryRepository;

    @Autowired
    private LocalUserPostCountRepository localUserPostCountRepository;

    @Autowired
    private LocalPostByUserRepository localPostByUserRepository;

    @Autowired
    private LocalPostMapper localPostMapper;

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
        if (!localPostRepository.existsById(resource.getId())) {
            localPostRepository.save(localPostMapper.mapToLocalPost(resource));

            Optional<LocalUserPostCount> optionalUser = localUserPostCountRepository.findById(resource.getUserId());
            if (optionalUser.isPresent()) {
                LocalUserPostCount user = optionalUser.get();
                user.incrementCount();
                localUserPostCountRepository.save(user);
            }

            localPostByUserRepository.save(localPostByUserMapper.mapToLocalPostUsers(resource.getUserId(), resource.getId()));

            List<Integer> userFollowers = grpcClientSocialService.getUserFollowers(resource.getUserId());
            feedEntryRepository.saveAll(userFollowers.stream().map(userId -> feedEntryMapper.mapToFeedEntry(resource.getId(), userId)).collect(Collectors.toList()));
            invertedFeedEntryRepository.saveAll(userFollowers.stream().map(userId -> feedEntryMapper.mapToInvertedFeedEntry(resource.getId(), userId)).collect(Collectors.toList()));
        }
    }

    @Override
    public void update(KafkaPostResource resource) {
        throw new NotImplementedException("Post updates are not available");
    }

    @Override
    public void delete(KafkaPostResource resource) {
        localPostRepository.deleteById(resource.getId());

        Optional<LocalUserPostCount> optionalUser = localUserPostCountRepository.findById(resource.getUserId());
        if (optionalUser.isPresent()) {
            LocalUserPostCount user = optionalUser.get();
            user.decrementCount();
            localUserPostCountRepository.save(user);
        }

        List<InvertedFeedEntry> allById = invertedFeedEntryRepository.findByPostId(resource.getId());

        for (InvertedFeedEntry invertedFeedEntry: allById) {
            feedEntryRepository.deleteByKeyUserIdAndKeyPostId(invertedFeedEntry.getUserId(), invertedFeedEntry.getPostId());
        }

        invertedFeedEntryRepository.deleteById(resource.getId());
    }
}
