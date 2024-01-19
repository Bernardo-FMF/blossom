package org.blossom.feed.mapper.impl;

import org.blossom.activitycontract.PostInfoResponse;
import org.blossom.feed.dto.MetadataDto;
import org.blossom.feed.mapper.interfac.ICompoundDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class MetadataDtoMapper implements ICompoundDtoMapper<Integer, PostInfoResponse, MetadataDto> {
    @Override
    public MetadataDto toDto(Integer entity, PostInfoResponse entity2) {
        return MetadataDto.builder()
                .userId(entity)
                .postId(entity2.getPostId())
                .commentCount(entity2.getTotalComments())
                .likeCount(entity2.getTotalLikes())
                .userLiked(entity2.getUserLikedPost())
                .userSaved(entity2.getUserSavedPost())
                .userCommented(entity2.getUserCommented())
                .build();
    }
}
