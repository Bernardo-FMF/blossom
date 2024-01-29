package org.blossom.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PaginationInfoDto {
    long totalPages;
    long currentPage;
    long totalElements;
    boolean eof;
}
