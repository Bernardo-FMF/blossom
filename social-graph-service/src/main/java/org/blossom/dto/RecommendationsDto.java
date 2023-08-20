package org.blossom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.blossom.kafka.inbound.model.LocalUser;

import java.util.List;

@AllArgsConstructor
@Builder
public class RecommendationsDto {
    private List<LocalUser> recommendations;
    boolean eof;
    long currentPage;
    long totalPages;
    long totalElements;
}
