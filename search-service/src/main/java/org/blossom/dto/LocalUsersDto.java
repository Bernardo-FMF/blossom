package org.blossom.dto;

import lombok.Builder;
import lombok.Getter;
import org.blossom.localmodel.LocalUser;

import java.util.Set;

@Getter
@Builder
public class LocalUsersDto {
    Set<LocalUser> localUsers;

    boolean eof;
}
