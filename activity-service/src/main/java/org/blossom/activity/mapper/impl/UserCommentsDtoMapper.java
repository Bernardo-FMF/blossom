package org.blossom.activity.mapper.impl;

import org.blossom.activity.dto.PaginationInfoDto;
import org.blossom.activity.dto.UserCommentsDto;
import org.blossom.activity.entity.Comment;
import org.blossom.activity.entity.LocalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserCommentsDtoMapper {
    @Autowired
    private CommentDtoMapper commentDtoMapper;

    public UserCommentsDto toDto(LocalUser user, List<Comment> content, PaginationInfoDto paginationInfo) {
        return UserCommentsDto.builder()
                .user(user)
                .comments(content.stream().map(comment -> commentDtoMapper.toDto(comment)).toList())
                .paginationInfo(paginationInfo)
                .build();
    }
}
