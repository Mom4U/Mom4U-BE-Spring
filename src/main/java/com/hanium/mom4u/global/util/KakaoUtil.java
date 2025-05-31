package com.hanium.mom4u.global.util;

import com.hanium.mom4u.global.security.dto.response.KakaoTokenDto;
import com.hanium.mom4u.global.security.dto.response.KakaoUserInfoResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class KakaoUtil {

    @Value("${spring.security.kakao.client_id}")
    private String clientId;

    @Value("${spring.security.kakao.redirect_uri}")
    private String redirectUri;

    private static final String KAUTH_TOKEN_URL_HOST = "https://kauth.kakao.com";
    private static final String KAPI_USER_URL_HOST = "https://kapi.kakao.com";

    public String buildLoginUrl() {
        return String.format(
                "%s/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code",
                KAUTH_TOKEN_URL_HOST, clientId, redirectUri
        );
    }

    public KakaoTokenDto requestAccessToken(String code) {
        WebClient webClient = WebClient.create(KAUTH_TOKEN_URL_HOST);
        // 코드 유효성 검증 추가
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("인가 코드(code)가 유효하지 않습니다.");
        }

        System.out.println("[DEBUG] client_id: " + clientId);
        System.out.println("[DEBUG] redirect_uri: " + redirectUri);
        System.out.println("[DEBUG] code: " + code);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("redirect_uri", redirectUri);
        formData.add("code", code);

        try {
            return webClient.post()
                    .uri("/oauth/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(formData)
                    .retrieve()
                    .bodyToMono(KakaoTokenDto.class)
                    .block();
        } catch (WebClientResponseException e) {
            System.err.println("카카오 토큰 요청 실패: " + e.getResponseBodyAsString());
            throw new RuntimeException("카카오 로그인 실패: " + e.getStatusCode());
        }
    }


    public KakaoUserInfoResponseDto requestProfile(String accessToken) {
        WebClient webClient = WebClient.builder()
                .baseUrl(KAPI_USER_URL_HOST)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build();

        return webClient.get()
                .uri("/v2/user/me")
                .retrieve()
                .bodyToMono(KakaoUserInfoResponseDto.class)
                .block();
    }
}
