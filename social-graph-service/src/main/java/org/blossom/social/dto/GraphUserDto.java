package org.blossom.social.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class GraphUserDto {
    private int userId;
    private List<UserDto> users;
    private PaginationInfoDto paginationInfo;
}
