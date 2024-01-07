package org.blossom.jwt;

import lombok.NoArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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

    public String parseRolesToString(Set<SimpleGrantedAuthority> roles) {
        return StringUtils.collectionToCommaDelimitedString(roles);
    }
}
