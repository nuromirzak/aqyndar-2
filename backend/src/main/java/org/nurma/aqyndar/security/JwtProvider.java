package org.nurma.aqyndar.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.nurma.aqyndar.entity.Role;
import org.nurma.aqyndar.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@Log4j2
public class JwtProvider {

    private static final int ACCESS_TOKEN_EXPIRATION_MINUTES = 5;
    private static final int REFRESH_TOKEN_EXPIRATION_DAYS = 30;
    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;

    public JwtProvider(
            @Value("${jwt.secret.access}") final String jwtAccessSecret,
            @Value("${jwt.secret.refresh}") final String jwtRefreshSecret
    ) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
    }

    public String generateAccessToken(final User user) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant accessExpirationInstant =
                now.plusMinutes(ACCESS_TOKEN_EXPIRATION_MINUTES).atZone(ZoneId.systemDefault()).toInstant();
        final Date accessExpiration = Date.from(accessExpirationInstant);
        return Jwts.builder()
                .subject(user.getEmail())
                .expiration(accessExpiration)
                .signWith(jwtAccessSecret)
                .claim("roles", user.getRoles().stream().map(Role::getName).toList())
                .claim("firstName", user.getFirstName())
                .compact();
    }

    public String generateRefreshToken(final User user) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant refreshExpirationInstant =
                now.plusDays(REFRESH_TOKEN_EXPIRATION_DAYS).atZone(ZoneId.systemDefault()).toInstant();
        final Date refreshExpiration = Date.from(refreshExpirationInstant);
        return Jwts.builder()
                .subject(user.getEmail())
                .expiration(refreshExpiration)
                .signWith(jwtRefreshSecret)
                .compact();
    }

    public boolean validateAccessToken(final String accessToken) {
        return validateToken(accessToken, jwtAccessSecret);
    }

    public boolean validateRefreshToken(final String refreshToken) {
        return validateToken(refreshToken, jwtRefreshSecret);
    }

    private boolean validateToken(final String token, final SecretKey secret) {
        try {
            Jwts.parser()
                    .verifyWith(secret)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException jwtException) {
            log.info("Cannot parse jwt: {}", jwtException.getMessage());
        } catch (Exception e) {
            log.error("Critical error: {}", e.getMessage());
        }
        return false;
    }

    public Claims getAccessClaims(final String token) {
        return getClaims(token, jwtAccessSecret);
    }

    public Claims getRefreshClaims(final String token) {
        return getClaims(token, jwtRefreshSecret);
    }

    private Claims getClaims(final String token, final SecretKey secret) {
        return Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

