package org.blossom.auth.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class UsersDto {
    Set<SimplifiedUserDto> users;
    PaginationInfoDto paginationInfo;
}