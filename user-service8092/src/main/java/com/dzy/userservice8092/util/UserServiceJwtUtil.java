package com.dzy.userservice8092.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class UserServiceJwtUtil {

    private final SecretKey accessKey;
    private final long accessExpiration;

    public UserServiceJwtUtil(@Value("${jwt.access-secret}") String accessSecretBase64,@Value("${jwt.access-expiration:900000}") long accessExpiration) {
        byte[] keyBytes = Base64.getDecoder().decode(accessSecretBase64);
        this.accessKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessExpiration = accessExpiration;
    }

    /**
     * 生成短期 Access Token
     */
    public String generateAccessToken(Long userId, String username, String role) {
        Date now = new Date();
        return Jwts.builder()
                .subject(userId.toString())
                .claim("username", username)
                .claim("role", role)
                .id(UUID.randomUUID().toString())   // jti
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessExpiration))
                .signWith(accessKey)
                .compact();
    }
}