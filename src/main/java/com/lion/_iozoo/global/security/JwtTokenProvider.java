package com.lion._iozoo.global.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long validityInMilliseconds;

    // application.yml에 적어둔 설정값을 가져와서 초기화
    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity-in-milliseconds}") long validityInMilliseconds) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.validityInMilliseconds = validityInMilliseconds;
    }

    // 유저의 ID를 받아 액세스 토큰을 생성하는 메서드
    public String createAccessToken(Long userId) {
        Date now = new Date();
        // 현재 시간 + 1시간(3600000ms)으로 만료 시간 설정
        Date validity = new Date(now.getTime() + this.validityInMilliseconds);

        return Jwts.builder()
                .subject(String.valueOf(userId)) // 토큰의 주인(subject)으로 유저 ID 저장
                .issuedAt(now)                   // 발급 시간
                .expiration(validity)            // 만료 시간
                .signWith(key)                   // 서버의 비밀키로 위조 방지
                .compact();
    }
}