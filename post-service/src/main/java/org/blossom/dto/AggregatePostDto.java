package org.blossom.dto;

import lombok.Builder;
import org.blossom.kafka.inbound.model.LocalUser;

import java.util.List;

@Builder
public class AggregatePostDto {
    private LocalUser user;
    private List<PostDto> posts;
    boolean eof;
    int page;
    int totalPages;
    long totalElements;
}
