package org.blossom.feed.mapper.impl;

import org.blossom.feed.dto.*;
import org.blossom.feed.entity.FeedEntry;
import org.blossom.feed.entity.LocalPostByUser;
import org.blossom.feed.entity.LocalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class FeedDtoMapper {
    @Autowired
    private LocalUserDtoMapper localUserDtoMapper;

    public <T> FeedDto toDto(List<T> feedEntries, LocalUserDto user, Map<Integer, LocalUser> allUsers, Map<String, MetadataDto> metadata, PaginationInfoDto paginationInfo) {
        return FeedDto.builder()
                .user(user)
                .posts(feedEntries.stream().map(entry -> buildLocalPostDto(allUsers, metadata, entry)).toList())
                .paginationInfo(paginationInfo)
                .build();
    }

    private <T> LocalPostDto buildLocalPostDto(Map<Integer, LocalUser> allUsers, Map<String, MetadataDto> metadata, T entry) {
        if (entry instanceof FeedEntry) {
            FeedEntry tmp = (FeedEntry) entry;
            return toDto(tmp, localUserDtoMapper.toDto(allUsers.get(tmp.getPostCreatorId())), metadata.get(tmp.getPostId()));
        } else if (entry instanceof LocalPostByUser) {
            LocalPostByUser tmp = (LocalPostByUser) entry;
            return toDto(tmp, localUserDtoMapper.toDto(allUsers.get(tmp.getUserId())), metadata.get(tmp.getPostId()));
        }
        return null;
    }

    private LocalPostDto toDto(FeedEntry entry, LocalUserDto localUser, MetadataDto metadataDto) {
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
