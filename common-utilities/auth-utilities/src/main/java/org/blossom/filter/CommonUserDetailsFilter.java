package org.blossom.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.blossom.jwt.RoleParser;
import org.blossom.model.CommonUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

public class CommonUserDetailsFilter extends OncePerRequestFilter {
    @Autowired
    RoleParser roleParser;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String username = extractUsernameFromHeader(request);
        Integer userId = extractUserIdFromHeader(request);
        Set<? extends GrantedAuthority> roles = extractRolesFromHeader(request);

        if (username != null && userId != null && roles != null) {
            UserDetails userDetails = buildUserDetails(username, userId, roles);

            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                    userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private UserDetails buildUserDetails(String username, Integer userId, Set<? extends GrantedAuthority> roles) {
        return new CommonUserDetails(userId, username, roles);
    }

    private Set<? extends GrantedAuthority> extractRolesFromHeader(HttpServletRequest request) {
        String roles = request.getHeader("userRoles");
        return roles == null ? null : roleParser.parseStringToRoles(roles);
    }

    private Integer extractUserIdFromHeader(HttpServletRequest request) {
        String userId = request.getHeader("userId");
        return userId == null ? null : Integer.parseInt(userId);
    }

    private String extractUsernameFromHeader(HttpServletRequest request) {
        return request.getHeader("username");
    }
}