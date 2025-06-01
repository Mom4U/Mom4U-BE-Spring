package com.hanium.mom4u.global.security.service;

import com.hanium.mom4u.domain.member.entity.Member;
import com.hanium.mom4u.domain.member.repository.MemberRepository;
import com.hanium.mom4u.global.exception.BusinessException;
import com.hanium.mom4u.global.response.StatusCode;
import com.hanium.mom4u.global.security.dto.response.LoginResponseDto;

import com.hanium.mom4u.global.util.RefreshTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenUtil refreshTokenUtil;
    private final MemberRepository memberRepository;

    public LoginResponseDto reissue(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new BusinessException(StatusCode.TOKEN_NOT_FOUND);
        }

        // 1. 토큰 유효성 검증 및 이메일 추출
        String email = jwtTokenProvider.getUserEmail(refreshToken);
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(StatusCode.TOKEN_INVALID);
        }

        // 2. Redis에 저장된 refreshToken과 일치하는지 확인
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(StatusCode.TOKEN_INVALID));
        String savedToken = refreshTokenUtil.getRefreshToken(member.getId());
        if (!refreshToken.equals(savedToken)) {
            throw new BusinessException(StatusCode.TOKEN_INVALID);
        }

        // 3. 새 accessToken 발급 (refreshToken rotation 정책이면 새로 발급)
        String newAccessToken = jwtTokenProvider.createAccessToken(member.getEmail(), "ROLE_USER");

        return new LoginResponseDto(
                member.getEmail(),
                member.getName(),
                member.getSocialType()
        );
    }
}
