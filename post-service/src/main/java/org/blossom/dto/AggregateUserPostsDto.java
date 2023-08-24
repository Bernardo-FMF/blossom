package org.blossom.dto;

import lombok.Builder;
import lombok.Getter;
import org.blossom.kafka.inbound.model.LocalUser;

import java.util.List;

@Getter
@Builder
public class AggregateUserPostsDto {
    private LocalUser user;
    private List<PostDto> posts;
    private boolean eof;
    private long currentPage;
    private long totalPages;
    private long totalElements;
}
