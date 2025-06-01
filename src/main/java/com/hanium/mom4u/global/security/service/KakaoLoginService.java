package com.hanium.mom4u.global.security.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;

@Slf4j
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
            log.error("카카오 API 에러 응답: {}", errorBody);
            String errorCode = kakaoUtil.extractKakaoErrorCode(errorBody); // KakaoUtil 메서드 호출
            throw kakaoUtil.mapKakaoErrorToBusinessException(errorCode); // KakaoUtil 메서드 호출
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
