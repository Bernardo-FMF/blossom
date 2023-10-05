package org.blossom.feed.service;

import jakarta.annotation.Nullable;
import org.blossom.feed.dto.*;
import org.blossom.feed.entity.*;
import org.blossom.feed.exception.UserNotFoundException;
import org.blossom.feed.grpc.service.GrpcClientActivityService;
import org.blossom.feed.grpc.service.GrpcClientSocialService;
import org.blossom.feed.mapper.LocalPostDtoMapper;
import org.blossom.feed.mapper.LocalUserDtoMapper;
import org.blossom.feed.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private LocalPostRepository localPostRepository;

    @Autowired
    private LocalUserDtoMapper localUserDtoMapper;

    @Autowired
    private LocalPostDtoMapper localPostDtoMapper;

    @Autowired
    private GrpcClientSocialService grpcClientSocialService;

    @Autowired
    private GrpcClientActivityService grpcClientActivityService;

    public FeedDto getUserFeed(int userId, SearchParametersDto searchParameters) throws UserNotFoundException, InterruptedException {
        Optional<LocalUser> optionalUser = localUserRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        LocalUser user = optionalUser.get();

        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit()) : Pageable.unpaged();

        long totalElements = feedEntryRepository.countByKeyUserId(userId);
        if (totalElements == 0) {
            return getFeedDto(localUserDtoMapper.mapToLocalUserDto(user), null, 0, null, null, searchParameters);
        }
        Slice<FeedEntry> feedEntries = feedEntryRepository.findByKeyUserId(userId, page);

        List<String> allPostIds = feedEntries.get().map(FeedEntry::getPostId).distinct().collect(Collectors.toList());
        List<Integer> allUserIds = feedEntries.get().map(FeedEntry::getUserId).distinct().collect(Collectors.toList());

        List<LocalPost> allPosts = localPostRepository.findAllById(allPostIds);

        Map<Integer, LocalUser> allUsersMap = localUserRepository.findAllById(allUserIds).stream().collect(Collectors.toMap(LocalUser::getId, localUser -> localUser));

        Map<String, MetadataDto> metadata = grpcClientActivityService.getMetadata(userId, allPosts.stream().map(LocalPost::getId).distinct().collect(Collectors.toList()));

        return getFeedDto(localUserDtoMapper.mapToLocalUserDto(user), new SliceImpl<>(allPosts, page, feedEntries.hasNext()), totalElements, allUsersMap, metadata, searchParameters);
    }

    public FeedDto getGenericFeed(SearchParametersDto searchParameters) throws InterruptedException {
        List<Integer> mostFollowed = grpcClientSocialService.getMostFollowed();
        if (mostFollowed.isEmpty()) {
            return getFeedDto(null, null, 0, null, null, searchParameters);
        }

        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit()) : Pageable.unpaged();

        List<LocalUser> allUsers = localUserRepository.findAllById(mostFollowed);
        List<LocalUserPostCount> allUsersCount = localUserPostCountRepository.findAllById(mostFollowed);

        Map<Integer, LocalUser> allUsersMap = allUsers.stream().collect(Collectors.toMap(LocalUser::getId, localUser -> localUser));

        long totalElements = allUsersCount.stream()
                .map(LocalUserPostCount::getPostCount)
                .reduce(0L, Long::sum);
        if (totalElements == 0L) {
            return getFeedDto(null, null, 0, null, null, searchParameters);
        }

        List<LocalPostByUser> allPostsByUser = localPostByUserRepository.findAllById(allUsersMap.keySet());
        Slice<LocalPost> posts = localPostRepository.findByIdIn(allPostsByUser.stream().map(LocalPostByUser::getPostId).toList(), page);

        Map<String, MetadataDto> metadata = grpcClientActivityService.getMetadata(null, posts.get().map(LocalPost::getId).distinct().collect(Collectors.toList()));

        return getFeedDto(null, posts, totalElements, allUsersMap, metadata, searchParameters);
    }

    private FeedDto getFeedDto(@Nullable LocalUserDto user, @Nullable Slice<LocalPost> posts, long totalElements, Map<Integer, LocalUser> allUsers, Map<String, MetadataDto> metadata, SearchParametersDto searchParameters) {
        List<LocalPostDto> postDtos = posts == null ? List.of() : posts.stream().map(post -> localPostDtoMapper.mapToLocalPostDto(post, localUserDtoMapper.mapToLocalUserDto(allUsers.get(post.getUserId())), metadata.get(post.getId()))).collect(Collectors.toList());

        return FeedDto.builder()
                .user(user)
                .posts(postDtos)
                .totalPages((int) Math.ceil((double) totalElements / searchParameters.getPageLimit()))
                .currentPage(searchParameters.getPage())
                .totalElements(totalElements)
                .eof(posts == null || !posts.hasNext())
                .build();
    }
}
