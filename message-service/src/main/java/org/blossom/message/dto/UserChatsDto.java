package org.blossom.message.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserChatsDto {
    private List<ChatDto> chats;
    private boolean eof;
    private long currentPage;
    private long totalPages;
    private long totalElements;
}
