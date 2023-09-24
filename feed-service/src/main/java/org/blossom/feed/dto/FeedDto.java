package org.blossom.feed.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class FeedDto {
    private LocalUserDto user;
    private List<LocalPostDto> posts;
    private boolean eof;
    private long currentPage;
    private long totalPages;
    private long totalElements;
}
