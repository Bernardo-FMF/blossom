package org.blossom.feed.mapper;

import org.blossom.feed.dto.FeedDto;
import org.blossom.feed.dto.LocalUserDto;
import org.blossom.feed.dto.MetadataDto;
import org.blossom.feed.dto.PaginationInfoDto;
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
    private LocalPostDtoMapper localPostDtoMapper;

    @Autowired
    private LocalUserDtoMapper localUserDtoMapper;

    public FeedDto toDto(List<FeedEntry> feedEntries, LocalUserDto user, Map<Integer, LocalUser> allUsers, Map<String, MetadataDto> metadata, PaginationInfoDto paginationInfo) {
        return FeedDto.builder()
                .user(user)
                .posts(feedEntries.stream().map(entry -> localPostDtoMapper.toDto(entry, localUserDtoMapper.mapToLocalUserDto(allUsers.get(entry.getPostCreatorId())), metadata.get(entry.getPostId()))).toList())
                .paginationInfo(paginationInfo)
                .build();
    }

    public FeedDto toDto(List<LocalPostByUser> feedEntries, Map<Integer, LocalUser> allUsers, Map<String, MetadataDto> metadata, PaginationInfoDto paginationInfo) {
        return FeedDto.builder()
                .user(null)
                .posts(feedEntries.stream().map(entry -> localPostDtoMapper.toDto(entry, localUserDtoMapper.mapToLocalUserDto(allUsers.get(entry.getUserId())), metadata.get(entry.getPostId()))).toList())
                .paginationInfo(paginationInfo)
                .build();
    }
}
