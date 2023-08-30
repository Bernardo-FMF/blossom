package org.blossom.social.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.blossom.social.kafka.inbound.model.LocalUser;

import java.util.List;

@Getter
@Setter
@Builder
public class GraphUserDto {
    private LocalUser user;
    private List<LocalUser> followers;
    private List<LocalUser> follows;
}
