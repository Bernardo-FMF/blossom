package org.blossom.post.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaginationInfoDto {
    private long totalPages;
    private long currentPage;
    private long totalElements;
    private boolean eof;
}
