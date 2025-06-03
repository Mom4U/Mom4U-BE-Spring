package com.hanium.mom4u.global.security.controller;

import com.hanium.mom4u.global.response.CommonResponse;
import com.hanium.mom4u.global.security.service.NaverLoginService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/naver")
@RequiredArgsConstructor
public class NaverLoginController {

    private final NaverLoginService naverLoginService;

    @GetMapping
    public ResponseEntity<CommonResponse<?>> getUrl() {
        return ResponseEntity.ok(
                CommonResponse.onSuccess(naverLoginService.getCode())
        );
    }

    @PostMapping
    public ResponseEntity<CommonResponse<?>> naverLogin(
            HttpServletResponse response,
            @RequestParam("code") String code,
            @RequestParam("state") String state) {
        return ResponseEntity.ok(
                CommonResponse.onSuccess(naverLoginService.loginWithNaver(response, code, state))
        );
    }
}
