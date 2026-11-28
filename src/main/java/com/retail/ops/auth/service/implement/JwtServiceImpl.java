package com.retail.ops.auth.service.implement;

import com.retail.ops.auth.entity.RefreshTokenEntity;
import com.retail.ops.auth.entity.UserEntity;
import com.retail.ops.auth.repository.RefreshTokenRepository;
import com.retail.ops.auth.service.JwtService;
import com.retail.ops.common.config.jwt.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtServiceImpl implements JwtService {
    final RefreshTokenRepository refreshTokenRepository;
    final JwtProperties jwtProperties;
     Key accessTokenKey;

    @Value("${jwt.secret}")
    String secretKey;

    @PostConstruct
    void init() {
        if (Objects.isNull(secretKey) || secretKey.length() < 32) {
            throw new IllegalStateException("JWT secret key must be at least 256 bits!");
        }

        this.accessTokenKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateAccessToken(UserEntity userEntity) {
        Instant now = Instant.now();
        Instant expiry = now.plus(jwtProperties.accessToken().expirationMinutes(), ChronoUnit.MINUTES);

        return Jwts.builder()
                .subject(userEntity.getId())
                .claim("username", userEntity.getUsername())
                .claim("role", userEntity.getEmployee().getRole())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(accessTokenKey)
                .compact();
    }

    @Override
    @Transactional
    public String generateRefreshToken(UserEntity userEntity) {
        Instant now = Instant.now();
        Instant expiry = now.plus(jwtProperties.refreshToken().expirationDays(), ChronoUnit.DAYS);

        String tokenId = UUID.randomUUID().toString();
        String tokenValue = generateSecureRandomToken(64);

        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .user(userEntity)
                .tokenId(tokenId)
                .tokenHash(hashToken(tokenValue))
                .createdAt(now)
                .expiresAt(expiry)
                .revokedAt(null)
                .build();

        refreshTokenRepository.save(refreshTokenEntity);

        return tokenValue;
    }

    @Override
    public String hashToken(String tokenValue) {
        return DigestUtils.sha256Hex(tokenValue);
    }

    @Override
    public Key getSigningKey() {
        return this.accessTokenKey;
    }

    private String generateSecureRandomToken(int length) {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder(length);
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        for (int i = 0; i < length; i++) {
            int idx = secureRandom.nextInt(chars.length());
            stringBuilder.append(chars.charAt(idx));
        }

        return stringBuilder.toString();
    }
}
