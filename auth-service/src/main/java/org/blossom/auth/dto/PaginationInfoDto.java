package org.blossom.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaginationInfoDto {
    long totalPages;
    long currentPage;
    long totalElements;
    boolean eof;
}
