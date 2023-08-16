package org.blossom.auth.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class UsersDto {
    Set<SimplifiedUserDto> users;
    long totalPages;
    long currentPage;
    long totalElements;
    boolean eof;
}