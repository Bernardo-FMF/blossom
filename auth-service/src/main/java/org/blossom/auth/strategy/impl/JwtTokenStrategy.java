package org.blossom.auth.strategy.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.blossom.auth.entity.User;
import org.blossom.auth.strategy.interfac.ITokenStrategy;
import org.blossom.jwt.RoleParser;
import org.blossom.model.dto.ValidatedUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenStrategy implements ITokenStrategy {
    @Autowired
    private RoleParser roleParser;

    @Value("${jwt.secret}")
    String secret;

    @Value("${jwt.refresh}")
    private int refresh;

    @Override
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        String userRoles = StringUtils.collectionToCommaDelimitedString(user.getAuthorities());
        claims.put("roles", userRoles);
        claims.put("username", user.getUsername());
        return createToken(claims, user.getId());
    }

    @Override
    public ValidatedUserDto validateToken(final String token) {
        Claims claims = extractClaims(token);

        if (isTokenExpired(token)) {
            throw new BadJwtException("Token is expired");
        }

        ValidatedUserDto validatedUserDto = new ValidatedUserDto();
        validatedUserDto.setUserId(Integer.parseInt(claims.getSubject()));
        validatedUserDto.setUsername(claims.get("username", String.class));
        validatedUserDto.setAuthorities(roleParser.parseStringToRoles(claims.get("roles", String.class)));

        return validatedUserDto;
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token) {
        Date expirationDate = extractClaims(token).getExpiration();
        return expirationDate.before(new Date());
    }

    private String createToken(Map<String, Object> claims, int userId) {
        long now = System.currentTimeMillis();
        Date validity = new Date(now + refresh);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date(now))
                .setExpiration(validity)
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}