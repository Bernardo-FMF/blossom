package org.blossom.feed.service;

import jakarta.annotation.Nullable;
import org.blossom.feed.dto.*;
import org.blossom.feed.entity.FeedEntry;
import org.blossom.feed.entity.LocalPostByUser;
import org.blossom.feed.entity.LocalUser;
import org.blossom.feed.entity.LocalUserPostCount;
import org.blossom.feed.exception.UserNotFoundException;
import org.blossom.feed.grpc.service.GrpcClientActivityService;
import org.blossom.feed.grpc.service.GrpcClientSocialService;
import org.blossom.feed.mapper.impl.FeedDtoMapper;
import org.blossom.feed.mapper.impl.LocalUserDtoMapper;
import org.blossom.feed.repository.FeedEntryRepository;
import org.blossom.feed.repository.LocalPostByUserRepository;
import org.blossom.feed.repository.LocalUserPostCountRepository;
import org.blossom.feed.repository.LocalUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FeedService {
    @Autowired
    private FeedEntryRepository feedEntryRepository;

    @Autowired
    private LocalUserRepository localUserRepository;

    @Autowired
    private LocalUserPostCountRepository localUserPostCountRepository;

    @Autowired
    private LocalPostByUserRepository localPostByUserRepository;

    @Autowired
    private LocalUserDtoMapper localUserDtoMapper;

    @Autowired
    private GrpcClientSocialService grpcClientSocialService;

    @Autowired
    private GrpcClientActivityService grpcClientActivityService;

    @Autowired
    private FeedDtoMapper feedDtoMapper;

    public FeedDto getUserFeed(int userId, SearchParametersDto searchParameters) throws UserNotFoundException, InterruptedException {
        Optional<LocalUser> optionalUser = localUserRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        LocalUser user = optionalUser.get();

        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit()) : Pageable.unpaged();

        long totalElements = feedEntryRepository.countByKeyUserId(userId);
        if (totalElements == 0) {
            return getEmptyFeedDto(localUserDtoMapper.toDto(user), searchParameters);
        }
        Slice<FeedEntry> feedEntries = feedEntryRepository.findByKeyUserId(userId, page);

        List<Integer> allUserIds = feedEntries.get().map(entry -> entry.getKey().getUserId()).distinct().collect(Collectors.toList());

        Map<Integer, LocalUser> allUsersMap = localUserRepository.findAllById(allUserIds).stream().collect(Collectors.toMap(LocalUser::getId, localUser -> localUser));

        Map<String, MetadataDto> metadata = grpcClientActivityService.getMetadata(userId, feedEntries.get().map(FeedEntry::getPostId).distinct().collect(Collectors.toList()));

        PaginationInfoDto paginationInfo = new PaginationInfoDto((int) Math.ceil((double) totalElements / searchParameters.getPageLimit()), searchParameters.getPage(), totalElements, true);
        return feedDtoMapper.toDto(feedEntries.getContent(), localUserDtoMapper.toDto(user), allUsersMap, metadata, paginationInfo);
    }

    public FeedDto getGenericFeed(SearchParametersDto searchParameters) throws InterruptedException {
        List<Integer> mostFollowed = grpcClientSocialService.getMostFollowed();
        if (mostFollowed.isEmpty()) {
            return getEmptyFeedDto(null, searchParameters);
        }

        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit()) : Pageable.unpaged();

        Map<Integer, LocalUser> allUsersMap = fetchAllUsersMap(mostFollowed);

        long totalElements = computeTotalElements(allUsersMap.keySet());
        if (totalElements == 0L) {
            return getEmptyFeedDto(null, searchParameters);
        }

        Slice<LocalPostByUser> posts = localPostByUserRepository.findByUserIdIn(allUsersMap.keySet(), page);

        Map<String, MetadataDto> metadata = grpcClientActivityService.getMetadata(null, posts.get().map(LocalPostByUser::getPostId).distinct().collect(Collectors.toList()));

        PaginationInfoDto paginationInfo = new PaginationInfoDto((int) Math.ceil((double) totalElements / searchParameters.getPageLimit()), searchParameters.getPage(), totalElements, true);
        return feedDtoMapper.toDto(posts.getContent(), null, allUsersMap, metadata, paginationInfo);
    }

    private Map<Integer, LocalUser> fetchAllUsersMap(List<Integer> mostFollowed) {
        List<LocalUser> allUsers = localUserRepository.findAllById(mostFollowed);
        return allUsers.stream().collect(Collectors.toMap(LocalUser::getId, localUser -> localUser));
    }

    private long computeTotalElements(Set<Integer> userIds) {
        List<LocalUserPostCount> allUsersCount = localUserPostCountRepository.findAllById(userIds);
        return allUsersCount.stream().map(LocalUserPostCount::getPostCount).reduce(0L, Long::sum);
    }

    private FeedDto getEmptyFeedDto(@Nullable LocalUserDto user, SearchParametersDto searchParameters) {
        PaginationInfoDto paginationInfo = new PaginationInfoDto(0, searchParameters.getPage(), 0, true);
        return feedDtoMapper.toDto(List.of(), user, null, null, paginationInfo);
    }
}
