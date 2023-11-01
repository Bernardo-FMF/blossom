package org.blossom.activity.dto;

import lombok.Builder;
import lombok.Getter;
import org.blossom.activity.entity.LocalUser;
import org.blossom.activity.enums.InteractionType;

import java.util.List;

@Builder
@Getter
public class UserInteractionsDto {
    private LocalUser user;
    private InteractionType interactionType;
    private List<InteractionDto> interactions;
    private boolean eof;
    private long currentPage;
    private long totalPages;
    private long totalElements;
}
