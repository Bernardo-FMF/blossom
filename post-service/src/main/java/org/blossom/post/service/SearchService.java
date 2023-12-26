package org.blossom.post.service;

import org.blossom.post.cache.LocalUserCacheService;
import org.blossom.post.dto.AggregatePostsDto;
import org.blossom.post.dto.SearchParametersDto;
import org.blossom.post.dto.UserDto;
import org.blossom.post.entity.Post;
import org.blossom.post.mapper.impl.AggregatePostsMapper;
import org.blossom.post.mapper.impl.PostUserMapper;
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
    private PostUserMapper postUserMapper;

    @Autowired
    private AggregatePostsMapper aggregatePostsMapper;

    public AggregatePostsDto postHashtagLookup(SearchParametersDto searchParameters) {
        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit()) : null;
        Page<Post> posts = postRepository.findByHashtagsIn(searchParameters.getQuery(), page);

        List<Integer> userIds = posts.getContent().stream().map(Post::getUserId).toList();

        Map<Integer, UserDto> allUsers = userIds.stream().map(id -> localUserCache.getFromCache(id))
                .collect(Collectors.toMap(UserDto::getId, user -> user));

        return aggregatePostsMapper.toPaginatedDto(posts.getContent(), allUsers, aggregatePostsMapper.createPaginationInfo(posts.getNumber(), posts.getTotalPages(), posts.getTotalElements(), !posts.hasNext()));
    }
}
