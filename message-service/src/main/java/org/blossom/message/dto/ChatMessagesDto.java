package org.blossom.message.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ChatMessagesDto {
    private ChatDto chat;
    private List<MessageDto> messageDtos;
    private PaginationInfoDto paginationInfo;
}
