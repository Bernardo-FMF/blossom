package org.blossom.social.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RecommendationsDto {
    private List<LocalUserDto> recommendations;
    boolean eof;
    long currentPage;
    long totalPages;
    long totalElements;
}
