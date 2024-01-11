package org.blossom.post.mapper.impl;

import org.blossom.activitycontract.PostInfoResponse;
import org.blossom.post.dto.MetadataDto;
import org.blossom.post.dto.PostWithUserDto;
import org.blossom.post.mapper.interfac.ICompoundDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class MetadataDtoMapper implements ICompoundDtoMapper<Integer, PostInfoResponse, MetadataDto> {
    public void setMetadata(PostWithUserDto dto, MetadataDto metadataDto) {
        dto.setMetadata(metadataDto);
    }

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
