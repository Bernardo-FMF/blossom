package org.blossom.activity.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class PostDto implements Serializable {
    private String postId;
    private int userId;
}