package org.blossom.auth.strategy.interfac;

import org.blossom.auth.entity.User;
import org.blossom.model.dto.TokenDto;
import org.springframework.security.oauth2.jwt.BadJwtException;

public interface ITokenStrategy {
    String generateToken(User user);
    TokenDto validateToken(String token) throws BadJwtException;
}
