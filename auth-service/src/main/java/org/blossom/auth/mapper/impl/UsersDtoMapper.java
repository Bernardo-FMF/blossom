package org.blossom.auth.mapper.impl;

import org.blossom.auth.dto.PaginationInfoDto;
import org.blossom.auth.dto.SimplifiedUserDto;
import org.blossom.auth.dto.UsersDto;
import org.blossom.auth.entity.User;
import org.blossom.auth.mapper.interfac.IPaginatedDtoMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class UsersDtoMapper implements IPaginatedDtoMapper<User, UsersDto, SimplifiedUserDto, PaginationInfoDto> {
    @Override
    public SimplifiedUserDto toDto(User entity) {
        return SimplifiedUserDto.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .fullName(entity.getFullName())
                .imageUrl(entity.getImageUrl())
                .build();
    }

    @Override
    public UsersDto toPaginatedDto(Collection<User> entities, PaginationInfoDto paginationInfo) {
        return UsersDto.builder()
                .users(entities.stream().map(this::toDto).collect(Collectors.toSet()))
                .paginationInfo(paginationInfo)
                .build();
    }
}
