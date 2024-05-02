package org.blossom.activity.mapper.impl;

import org.blossom.activity.dto.MetadataDto;
import org.blossom.activity.mapper.interfac.IDtoMapper;
import org.blossom.activitycontract.PostInfoResponse;
import org.springframework.stereotype.Component;

@Component
public class MetadataGrpcMapper implements IDtoMapper<MetadataDto, PostInfoResponse> {
    @Override
    public PostInfoResponse toDto(MetadataDto entity) {
        return PostInfoResponse.newBuilder()
                .setPostId(entity.getPostId())
                .setUserCommented(entity.getCommentMetadata().isUserCommented())
                .setUserSavedPost(entity.getInteractionMetadata().isUserSaved())
                .setUserLikedPost(entity.getInteractionMetadata().isUserLiked())
                .setTotalLikes(entity.getInteractionMetadata().getLikeCount())
                .setTotalComments(entity.getCommentMetadata().getCommentCount())
                .build();
    }
}
