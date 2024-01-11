package org.blossom.post.service;

import org.blossom.post.cache.LocalUserCacheService;
import org.blossom.post.dto.AggregatePostsDto;
import org.blossom.post.dto.MetadataDto;
import org.blossom.post.dto.SearchParametersDto;
import org.blossom.post.dto.UserDto;
import org.blossom.post.entity.Post;
import org.blossom.post.grpc.service.GrpcClientActivityService;
import org.blossom.post.mapper.impl.AggregatePostsMapper;
import org.blossom.post.mapper.impl.PostUserDtoMapper;
import org.blossom.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SearchService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LocalUserCacheService localUserCache;

    @Autowired
    private PostUserDtoMapper postUserDtoMapper;

    @Autowired
    private AggregatePostsMapper aggregatePostsMapper;

    @Autowired
    private GrpcClientActivityService grpcClientActivityService;

    public AggregatePostsDto postHashtagLookup(Integer userId, SearchParametersDto searchParameters) throws InterruptedException {
        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit()) : Pageable.unpaged();
        Page<Post> posts = postRepository.findByHashtagsIn(searchParameters.getQuery(), page);

        List<Integer> userIds = posts.getContent().stream().map(Post::getUserId).toList();

        Map<Integer, UserDto> allUsers = userIds.stream().distinct().map(id -> localUserCache.getFromCache(id))
                .collect(Collectors.toMap(UserDto::getId, user -> user));

        Map<String, MetadataDto> metadata = grpcClientActivityService.getMetadata(userId, posts.stream().map(Post::getId).distinct().collect(Collectors.toList()));

        return aggregatePostsMapper.toPaginatedDto(
                posts.getContent(),
                allUsers,
                metadata,
                aggregatePostsMapper.createPaginationInfo(posts.getNumber(), posts.getTotalPages(), posts.getTotalElements(), !posts.hasNext()));
    }
}
