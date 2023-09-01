package org.blossom.social.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RecommendationsDto {
    private int userId;
    private List<LocalUserDto> recommendations;
    private boolean eof;
    private long currentPage;
    private long totalPages;
    private long totalElements;
}
