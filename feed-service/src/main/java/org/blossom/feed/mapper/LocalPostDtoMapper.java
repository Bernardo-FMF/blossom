package org.blossom.feed.mapper;

import org.blossom.feed.dto.LocalPostDto;
import org.blossom.feed.dto.LocalUserDto;
import org.blossom.feed.dto.MetadataDto;
import org.blossom.feed.entity.LocalPost;
import org.springframework.stereotype.Component;

@Component
public class LocalPostDtoMapper {
    public LocalPostDto mapToLocalPostDto(LocalPost localPost, LocalUserDto localUser, MetadataDto metadataDto) {
        return LocalPostDto.builder()
                .id(localPost.getId())
                .creator(localUser)
                .media(localPost.getMedia().toArray(String[]::new))
                .description(localPost.getDescription())
                .metadata(metadataDto)
                .build();
    }
}
