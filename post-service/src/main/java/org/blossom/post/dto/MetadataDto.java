package org.blossom.post.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MetadataDto {
    private String postId;
    private Integer userId;
    private long likeCount;
    private long commentCount;
    private boolean userCommented;
    private boolean userLiked;
    private boolean userSaved;
}