package com.hanium.mom4u.global.security.service;

import com.hanium.mom4u.domain.member.common.Role;
import com.hanium.mom4u.domain.member.common.SocialType;
import com.hanium.mom4u.domain.member.entity.Member;
import com.hanium.mom4u.domain.member.repository.MemberRepository;
import com.hanium.mom4u.global.security.dto.response.KakaoLoginResult;
import com.hanium.mom4u.global.security.dto.response.KakaoTokenDto;
import com.hanium.mom4u.global.security.dto.response.KakaoUserInfoResponseDto;
import com.hanium.mom4u.global.security.dto.response.LoginResponseDto;
import com.hanium.mom4u.global.security.jwt.JwtTokenProvider;
import com.hanium.mom4u.global.util.KakaoUtil;
import com.hanium.mom4u.global.util.RefreshTokenUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoLoginService {

    private final KakaoUtil kakaoUtil;
    private final RefreshTokenUtil refreshTokenUtil;
    private final JwtTokenProvider jwtTokenProvider;

    private final MemberRepository memberRepository;

    // 1. 카카오 로그인 URL 반환
    public String getLoginUrl() {
        return kakaoUtil.buildLoginUrl();
    }

    // 2. code로 로그인 처리
    public KakaoLoginResult login(HttpServletResponse response, String code) {
        try {
            KakaoTokenDto tokenDto = kakaoUtil.requestAccessToken(code);
            KakaoUserInfoResponseDto userInfo = kakaoUtil.requestProfile(tokenDto.getAccessToken());
            return loginWithKakao(response, userInfo); // KakaoLoginResult 반환
        } catch (WebClientResponseException e) {
            String errorBody = e.getResponseBodyAsString();
            log.error("카카오 API 에러 응답: {}", errorBody);
            String errorCode = kakaoUtil.extractKakaoErrorCode(errorBody);
            throw kakaoUtil.mapKakaoErrorToBusinessException(errorCode);
        }
    }

    // 반환 타입 변경
    private KakaoLoginResult loginWithKakao(HttpServletResponse response, KakaoUserInfoResponseDto userInfo) {
        String email = userInfo.getKakaoAccount().getEmail();
        String nickname = userInfo.getKakaoAccount().getProfile().getNickname();

        Member member = memberRepository.findByEmailAndSocialType(email, SocialType.KAKAO)
                .orElseGet(() -> {
                    Member newMember = new Member();
                    newMember.setEmail(email);
                    newMember.setName(nickname);
                    newMember.setRole(Role.ROLE_USER);
                    newMember.setSocialType(SocialType.KAKAO);
                    return memberRepository.save(newMember);
                });

        // 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(member.getEmail(), member.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getEmail());

        // Redis에 리프레시 토큰 저장
        refreshTokenUtil.saveRefreshToken(member.getId(), refreshToken);
        refreshTokenUtil.addRefreshTokenCookie(response, refreshToken);

        // 사용자 정보 DTO 생성
        LoginResponseDto userInfoDto = new LoginResponseDto(
                accessToken,
                member.getEmail(),
                member.getName(),
                member.getSocialType()
        );

        // KakaoLoginResult로 토큰과 사용자 정보 함께 반환
        return new KakaoLoginResult(accessToken, refreshToken, userInfoDto);
    }
}
