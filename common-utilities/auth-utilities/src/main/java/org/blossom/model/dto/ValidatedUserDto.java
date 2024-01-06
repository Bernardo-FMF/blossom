package org.blossom.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

@Getter
@Setter
public class ValidatedUserDto {
    int userId;
    String username;
    Set<SimpleGrantedAuthority> authorities;
}
