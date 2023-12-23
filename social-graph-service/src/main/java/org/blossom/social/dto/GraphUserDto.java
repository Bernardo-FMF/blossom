package org.blossom.social.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GraphUserDto {
    private int userId;
    private List<UserDto> otherUsers;
    private long totalPages;
    private long currentPage;
    private long totalElements;
    private boolean eof;
}
