package org.blossom.social.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class GraphUserDto {
    private LocalUserDto user;
    private List<LocalUserDto> followers;
    private List<LocalUserDto> follows;
}
