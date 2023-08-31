package org.blossom.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.blossom.kafka.inbound.model.LocalUser;

import java.util.Date;

@Getter
@Setter
@Builder
public class PostWithUserDto {
    private String id;
    private LocalUser user;
    private String[] mediaUrls;
    private String[] hashtags;
    private String description;
    private Date createdAt;
}
