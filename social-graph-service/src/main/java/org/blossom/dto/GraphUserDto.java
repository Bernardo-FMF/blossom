package org.blossom.dto;

import lombok.Builder;
import org.blossom.kafka.inbound.model.LocalUser;

import java.util.Set;

@Builder
public class GraphUserDto {
    private LocalUser user;
    private Set<LocalUser> followers;
    private Set<LocalUser> follows;
}
