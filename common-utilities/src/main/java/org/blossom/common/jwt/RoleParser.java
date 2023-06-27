package org.blossom.common.jwt;

import lombok.NoArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@Component
public class RoleParser {
    public Set<SimpleGrantedAuthority> parseStringToRoles(String roles) {
        return Arrays.stream(roles.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}
