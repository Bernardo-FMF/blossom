package org.blossom.activity.mapper.impl;

import org.blossom.activity.dto.PaginationInfoDto;
import org.blossom.activity.dto.PostCommentsDto;
import org.blossom.activity.dto.PostDto;
import org.blossom.activity.entity.Comment;
import org.blossom.activity.entity.LocalUser;
import org.blossom.activity.projection.CommentProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PostCommentsDtoMapper {
    @Autowired
    private CommentDtoMapper commentDtoMapper;

    public PostCommentsDto toDto(PostDto entity, List<Comment> content, PaginationInfoDto paginationInfo) {
        return PostCommentsDto.builder()
                .post(entity)
                .comments(content.stream().map(comment -> commentDtoMapper.toDto(comment)).toList())
                .paginationInfo(paginationInfo)
                .build();
    }

    public PostCommentsDto toDto(PostDto entity, List<CommentProjection> content, Map<Integer, LocalUser> allUsers, PaginationInfoDto paginationInfo) {
        return PostCommentsDto.builder()
                .post(entity)
                .comments(content.stream().map(comment -> commentDtoMapper.toDto(comment, allUsers.get(comment.getUserId()))).toList())
                .paginationInfo(paginationInfo)
                .build();
    }
}
