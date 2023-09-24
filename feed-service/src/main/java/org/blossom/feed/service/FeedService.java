package org.blossom.feed.service;

import org.blossom.feed.dto.FeedDto;
import org.blossom.feed.dto.MetadataDto;
import org.blossom.feed.dto.SearchParametersDto;
import org.blossom.feed.entity.FeedEntry;
import org.blossom.feed.entity.LocalPost;
import org.blossom.feed.entity.LocalUser;
import org.blossom.feed.exception.UserNotFoundException;
import org.blossom.feed.grpc.service.GrpcClientActivityService;
import org.blossom.feed.grpc.service.GrpcClientSocialService;
import org.blossom.feed.mapper.LocalPostDtoMapper;
import org.blossom.feed.mapper.LocalUserDtoMapper;
import org.blossom.feed.repository.FeedEntryRepository;
import org.blossom.feed.repository.LocalPostRepository;
import org.blossom.feed.repository.LocalUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
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

        Slice<FeedEntry> feedEntries = feedEntryRepository.findByUserId(userId, page);
        long totalElements = feedEntryRepository.countByUserId(userId);

        List<String> allPostIds = feedEntries.get().map(FeedEntry::getPostId).distinct().collect(Collectors.toList());
        List<Integer> allUserIds = feedEntries.get().map(FeedEntry::getUserId).distinct().collect(Collectors.toList());

        List<LocalPost> allPosts = localPostRepository.findAllById(allPostIds);
        Map<Integer, LocalUser> allUsers = localUserRepository.findAllById(allUserIds).stream().collect(Collectors.toMap(LocalUser::getId, localUser -> localUser));

        Map<String, MetadataDto> metadata = grpcClientActivityService.getMetadata(userId, allPosts.stream().map(LocalPost::getId).distinct().collect(Collectors.toList()));

        return FeedDto.builder()
                .user(localUserDtoMapper.mapToLocalUserDto(user))
                .posts(allPosts.stream().map(post -> localPostDtoMapper.mapToLocalPostDto(post, localUserDtoMapper.mapToLocalUserDto(allUsers.get(post.getUserId())), metadata.get(post.getId()))).collect(Collectors.toList()))
                .totalPages((int) Math.ceil((double) totalElements / searchParameters.getPageLimit()))
                .currentPage(searchParameters.getPage())
                .totalElements(totalElements)
                .eof(!feedEntries.hasNext())
                .build();
    }

    public FeedDto getGenericFeed(SearchParametersDto searchParameters) throws InterruptedException {
        List<Integer> mostFollowed = grpcClientSocialService.getMostFollowed();

        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit(), Sort.by(Sort.Direction.DESC, "createdAt")) : Pageable.unpaged();
        Slice<LocalPost> posts = localPostRepository.findByUserIdIn(mostFollowed, page);
        long totalElements = localPostRepository.countByUserIdIn(mostFollowed);

        Map<Integer, LocalUser> allUsers = localUserRepository.findAllById(mostFollowed).stream().collect(Collectors.toMap(LocalUser::getId, localUser -> localUser));

        Map<String, MetadataDto> metadata = grpcClientActivityService.getMetadata(null, posts.get().map(LocalPost::getId).distinct().collect(Collectors.toList()));

        return FeedDto.builder()
                .posts(posts.stream().map(post -> localPostDtoMapper.mapToLocalPostDto(post, localUserDtoMapper.mapToLocalUserDto(allUsers.get(post.getUserId())), metadata.get(post.getId()))).collect(Collectors.toList()))
                .totalPages((int) Math.ceil((double) totalElements / searchParameters.getPageLimit()))
                .currentPage(searchParameters.getPage())
                .totalElements(totalElements)
                .eof(!posts.hasNext())
                .build();
    }
}
