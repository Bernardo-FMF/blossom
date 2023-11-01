package org.blossom.post.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostIdentifierDto {
    private String postId;
    private int userId;
}
