package com.hanium.mom4u.global.security.controller;

import com.hanium.mom4u.global.response.CommonResponse;
import com.hanium.mom4u.global.security.dto.response.LoginResponseDto;
import com.hanium.mom4u.global.security.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth API Controller")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Access Token 재발급",
            description = "쿠키에 담긴 Refresh Token을 사용해 새로운 Access Token을 발급받습니다.",
            security = @SecurityRequirement(name = "JWT"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Access Token 재발급 성공",
                            content = @Content(schema = @Schema(implementation = LoginResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Refresh Token이 없거나 유효하지 않음",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                              "httpStatus": 400,
                                              "message": "토큰을 찾을 수 없습니다.",
                                              "data": null,
                                              "success": false
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "유효하지 않은 Refresh Token",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                              "httpStatus": 401,
                                              "message": "유효하지 않은 토큰입니다.",
                                              "data": null,
                                              "success": false
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 내부 에러",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                              "httpStatus": "INTERNAL_SERVER_ERROR",
                                              "code": "SERVER5001",
                                              "message": "서버에서 에러가 발생했습니다."
                                            }
                                            """)
                            )
                    )
            }
    )
    @PostMapping("/refresh")
    public CommonResponse<LoginResponseDto> reissue(
            @CookieValue(value = "refreshToken", required = false) String refreshToken
    ) {
        LoginResponseDto dto = authService.reissue(refreshToken);
        return CommonResponse.onSuccess(dto);
    }
}
