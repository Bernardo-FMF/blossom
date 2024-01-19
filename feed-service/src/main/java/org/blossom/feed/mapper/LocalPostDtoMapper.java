package org.blossom.feed.mapper;

import org.blossom.feed.dto.LocalPostDto;
import org.blossom.feed.dto.LocalUserDto;
import org.blossom.feed.dto.MetadataDto;
import org.blossom.feed.entity.FeedEntry;
import org.blossom.feed.entity.LocalPostByUser;
import org.springframework.stereotype.Component;

@Component
public class LocalPostDtoMapper {
    public LocalPostDto toDto(FeedEntry entry, LocalUserDto localUser, MetadataDto metadataDto) {
        return LocalPostDto.builder()
                .id(entry.getPostId())
                .creator(localUser)
                .media(entry.getMedia().toArray(new String[0]))
                .description(entry.getDescription())
                .metadata(metadataDto)
                .build();
    }

    public LocalPostDto toDto(LocalPostByUser entry, LocalUserDto localUser, MetadataDto metadataDto) {
        return LocalPostDto.builder()
                .id(entry.getPostId())
                .creator(localUser)
                .media(entry.getMedia().toArray(new String[0]))
                .description(entry.getDescription())
                .metadata(metadataDto)
                .build();
    }
}
