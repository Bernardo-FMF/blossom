package org.blossom.service;

import org.blossom.cache.LocalUserCacheService;
import org.blossom.dto.AggregatePostsDto;
import org.blossom.dto.SearchParametersDto;
import org.blossom.entity.Post;
import org.blossom.kafka.inbound.model.LocalUser;
import org.blossom.mapper.PostDtoMapper;
import org.blossom.repository.PostRepository;
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

        List<String> userIds = posts.getContent().stream().map(post -> String.valueOf(post.getUserId())).toList();
        Map<Integer, LocalUser> allUsers = localUserCache.getMultiFromCache(userIds).stream()
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
