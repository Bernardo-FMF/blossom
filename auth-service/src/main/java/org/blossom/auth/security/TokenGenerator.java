package org.blossom.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.blossom.auth.entity.User;
import org.blossom.common.jwt.RoleParser;
import org.blossom.common.model.dto.TokenDto;
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
public class TokenGenerator {
    @Autowired
    private RoleParser roleParser;

    @Value("${jwt.secret}")
    String secret;

    @Value("${jwt.refresh}")
    private int refresh;

    public TokenDto validateToken(final String token) {
        Claims claims = extractClaims(token);

        if (isTokenExpired(token)) {
            throw new BadJwtException("Token is expired");
        }

        TokenDto tokenDto = new TokenDto();
        tokenDto.setUserId(Integer.parseInt(claims.getSubject()));
        tokenDto.setUsername(claims.get("username", String.class));
        tokenDto.setAuthorities(roleParser.parseStringToRoles(claims.get("roles", String.class)));

        return tokenDto;
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token) {
        Date expirationDate = extractClaims(token).getExpiration();
        return expirationDate.before(new Date());
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        String userRoles = StringUtils.collectionToCommaDelimitedString(user.getAuthorities());
        claims.put("roles", userRoles);
        claims.put("username", user.getUsername());
        return createToken(claims, user.getId());
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