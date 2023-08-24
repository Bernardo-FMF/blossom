package org.blossom.kafka.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class LocalPost implements Serializable {
    private String postId;
    private int userId;
}
