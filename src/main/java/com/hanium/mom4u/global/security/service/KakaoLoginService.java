package com.hanium.mom4u.global.security.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanium.mom4u.domain.member.common.SocialType;
import com.hanium.mom4u.domain.member.entity.Member;
import com.hanium.mom4u.domain.member.repository.MemberRepository;
import com.hanium.mom4u.global.exception.BusinessException;
import com.hanium.mom4u.global.exception.GeneralException;
import com.hanium.mom4u.global.response.StatusCode;
import com.hanium.mom4u.global.security.dto.response.KakaoTokenDto;
import com.hanium.mom4u.global.security.dto.response.KakaoUserInfoResponseDto;
import com.hanium.mom4u.global.security.dto.response.LoginResponseDto;
import com.hanium.mom4u.global.util.KakaoUtil;
import com.hanium.mom4u.global.util.RefreshTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class KakaoLoginService {

    private final KakaoUtil kakaoUtil;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenUtil refreshTokenUtil;

    // 1. 카카오 로그인 URL 반환
    public String getLoginUrl() {
        return kakaoUtil.buildLoginUrl();
    }

    // 2. code로 로그인 처리
    public LoginResponseDto login(String code) {
        try {
            KakaoTokenDto tokenDto = kakaoUtil.requestAccessToken(code);
            KakaoUserInfoResponseDto userInfo = kakaoUtil.requestProfile(tokenDto.getAccessToken());
            return loginWithKakao(userInfo);
        } catch (WebClientResponseException e) {
            String errorBody = e.getResponseBodyAsString();
            String errorCode = extractKakaoErrorCode(errorBody); // JSON 파싱 메서드
            handleKakaoError(errorCode); // 에러 코드 기반 처리
        } catch (Exception e) {
            throw GeneralException.of(StatusCode.INTERNAL_SERVER_ERROR);
        }
        return null;
    }

    private String extractKakaoErrorCode(String errorBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(errorBody);
            return rootNode.path("error_code").asText();
        } catch (IOException ex) {
            return "UNKNOWN_ERROR";
        }
    }

    private void handleKakaoError(String errorCode) {
        switch (errorCode) {
            case "KOE320":
                throw BusinessException.of(StatusCode.KAKAO_AUTH_CODE_INVALID);
            case "KOE303":
                throw BusinessException.of(StatusCode.KAKAO_REDIRECT_URI_MISMATCH);
            case "KOE101":
                throw BusinessException.of(StatusCode.KAKAO_CLIENT_INVALID);
            case "KOE102":
                throw BusinessException.of(StatusCode.KAKAO_INVALID_REQUEST);
            case "KOE006":
                throw BusinessException.of(StatusCode.KAKAO_REDIRECT_URI_INVALID);
            default:
                throw GeneralException.of(StatusCode.KAKAO_SERVER_ERROR);
        }
    }

    // 3. 유저 정보로 로그인 처리 (내부 메서드)
    private LoginResponseDto loginWithKakao(KakaoUserInfoResponseDto userInfo) {
        String email = userInfo.getKakaoAccount().getEmail();
        String name = userInfo.getKakaoAccount().getName();

        Member member = memberRepository.findByEmailAndSocialType(email, SocialType.KAKAO)
                .orElseGet(() -> {
                    Member newMember = new Member();
                    newMember.setEmail(email);
                    newMember.setName(name);
                    newMember.setSocialType(SocialType.KAKAO);
                    return memberRepository.save(newMember);
                });

        String accessToken = jwtTokenProvider.createAccessToken(member.getEmail(), "ROLE_USER");
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getEmail());

        // Redis에 리프레시 토큰 저장 (추가)
        refreshTokenUtil.saveRefreshToken(member.getId(), refreshToken);
        return new LoginResponseDto(accessToken, refreshToken, member.getEmail(), member.getName(),member.getSocialType());
    }



}
