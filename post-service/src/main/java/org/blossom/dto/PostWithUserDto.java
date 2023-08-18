package org.blossom.dto;

import lombok.*;
import org.blossom.kafka.inbound.model.LocalUser;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PostWithUserDto {
    private String id;
    private LocalUser user;
    private String[] mediaUrls;
    private String[] hashtags;
    private String description;
    private LocalDateTime createdAt;
}
