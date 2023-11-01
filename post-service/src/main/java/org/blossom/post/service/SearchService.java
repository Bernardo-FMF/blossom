package org.blossom.post.service;

import org.blossom.post.cache.LocalUserCacheService;
import org.blossom.post.dto.AggregatePostsDto;
import org.blossom.post.dto.SearchParametersDto;
import org.blossom.post.entity.Post;
import org.blossom.post.kafka.inbound.model.LocalUser;
import org.blossom.post.mapper.PostDtoMapper;
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
    private PostDtoMapper postDtoMapper;

    public AggregatePostsDto postHashtagLookup(SearchParametersDto searchParameters) {
        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit()) : null;
        Page<Post> posts = postRepository.findByHashtagsIn(searchParameters.getQuery(), page);

        List<Integer> userIds = posts.getContent().stream().map(Post::getUserId).toList();

        Map<Integer, LocalUser> allUsers = userIds.stream().map(id -> localUserCache.getFromCache(id))
                .collect(Collectors.toMap(LocalUser::getId, user -> user));

        return AggregatePostsDto.builder()
                .posts(posts.get().map(post -> postDtoMapper.mapToPostWithUserDto(post, allUsers.get(post.getUserId()))).collect(Collectors.toList()))
                .currentPage(posts.getNumber())
                .totalPages(posts.getTotalPages())
                .totalElements(posts.getTotalElements())
                .eof(!posts.hasNext())
                .build();
    }
}
