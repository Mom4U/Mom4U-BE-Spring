package com.hanium.mom4u.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenUtil {
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.jwt.access.expiration}")
    private long accessTokenExpiration;

    @Value("${spring.jwt.refresh.expiration}")
    private long refreshTokenExpiration;


    // 저장
    public void saveRefreshToken(Long memberId, String refreshToken) {
        String key = "refreshToken:" + memberId;
        redisTemplate.opsForValue().set(key, refreshToken, refreshTokenExpiration, TimeUnit.SECONDS);
    }

    // 조회
    public String getRefreshToken(Long memberId) {
        return redisTemplate.opsForValue().get("refreshToken:" + memberId);
    }

    // 삭제
    public void deleteRefreshToken(Long memberId) {
        redisTemplate.delete("refreshToken:" + memberId);
    }

    // 쿠키에 추가
    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) refreshTokenExpiration);
        response.addCookie(cookie);
    }

    // 쿠키 삭제
    public void removeRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }



    // RefreshTokenUtil.java
    public void addAccessTokenCookie(HttpServletResponse response, String accessToken) {
        Cookie cookie = new Cookie("accessToken", accessToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) accessTokenExpiration);

        // SameSite=Lax 또는 Strict 설정 (Spring Boot 2.4+)
        response.setHeader("Set-Cookie",
                String.format("%s=%s; Path=%s; HttpOnly; Secure; SameSite=Lax",
                        cookie.getName(),
                        cookie.getValue(),
                        cookie.getPath()));
    }

}
