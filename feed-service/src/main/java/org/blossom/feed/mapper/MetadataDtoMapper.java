package org.blossom.feed.mapper;

import org.blossom.activitycontract.PostInfoResponse;
import org.blossom.feed.dto.MetadataDto;
import org.springframework.stereotype.Component;

@Component
public class MetadataDtoMapper {
    public MetadataDto mapToMetadataDto(Integer userId, PostInfoResponse postInfoResponse) {
        return MetadataDto.builder()
                .userId(userId)
                .postId(postInfoResponse.getPostId())
                .commentCount(postInfoResponse.getTotalComments())
                .likeCount(postInfoResponse.getTotalLikes())
                .userLiked(postInfoResponse.getUserLikedPost())
                .userSaved(postInfoResponse.getUserSavedPost())
                .userCommented(postInfoResponse.getUserCommented())
                .build();
    }
}
