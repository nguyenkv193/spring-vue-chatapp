package com.example.chatapp.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public boolean isConfigured() {
        return jwtProperties.isConfigured();
    }

    public String generateAccessToken(UUID userId, String email) {
        if (!isConfigured()) {
            throw new IllegalStateException("APP_JWT_SECRET must be configured before issuing tokens.");
        }

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(jwtProperties.getAccessTokenTtlMinutes() * 60);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .issuer(jwtProperties.getIssuer())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(signingKey())
                .compact();
    }

    public JwtPrincipal parse(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return new JwtPrincipal(
                UUID.fromString(claims.getSubject()),
                claims.get("email", String.class)
        );
    }

    public boolean isTokenValid(String token) {
        if (!isConfigured()) {
            return false;
        }
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    public Instant getExpirationInstant() {
        return Instant.now().plusSeconds(jwtProperties.getAccessTokenTtlMinutes() * 60);
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}
