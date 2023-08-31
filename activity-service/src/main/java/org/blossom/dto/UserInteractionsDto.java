package org.blossom.dto;

import lombok.Builder;
import org.blossom.entity.LocalUser;
import org.blossom.enums.InteractionType;

import java.util.List;

@Builder
public class UserInteractionsDto {
    private LocalUser user;
    private InteractionType interactionType;
    private List<InteractionDto> interactions;
    private boolean eof;
    private long currentPage;
    private long totalPages;
    private long totalElements;
}
