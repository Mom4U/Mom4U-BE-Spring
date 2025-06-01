package com.hanium.mom4u.global.response;

import org.springframework.http.HttpStatus;

public enum StatusCode {

    // ==================================================================
    SUCCESS_TEST(HttpStatus.OK, "TEST", "테스트 성공에 대한 응답입니다."),

    // ==================================================================
    // test
    FAILURE_TEST(HttpStatus.INTERNAL_SERVER_ERROR, "TESTERROR", "테스트 실패에 대한 응답입니다."),

    // token
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "TE4001", "토큰을 찾을 수 없습니다."),

    // server
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER5001", "서버에서 에러가 발생했습니다."),


    INVALID_JSON_FORMAT(HttpStatus.BAD_REQUEST, "JSON400", "JSON 형식 오류"),
    NETWORK_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "NET500", "네트워크 오류 발생"),

    //kakao login
    KAKAO_AUTH_CODE_INVALID(HttpStatus.BAD_REQUEST, "KAKAO320", "카카오 인가 코드가 이미 사용되었거나 만료되었습니다."),
    KAKAO_REDIRECT_URI_MISMATCH(HttpStatus.BAD_REQUEST, "KAKAO303", "카카오 redirect_uri가 일치하지 않습니다."),
    KAKAO_LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "KAKAO401", "카카오 로그인에 실패했습니다."),
    // 카카오 에러 확장
    KAKAO_CLIENT_INVALID(HttpStatus.UNAUTHORIZED, "KAKAO101", "잘못된 클라이언트 정보입니다"),
    KAKAO_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "KAKAO102", "잘못된 요청 형식입니다"),
    KAKAO_REDIRECT_URI_INVALID(HttpStatus.BAD_REQUEST, "KAKAO006", "등록되지 않은 Redirect URI입니다"),
    KAKAO_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "KAKAO500", "카카오 서버 오류가 발생했습니다"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String description;

    StatusCode(HttpStatus httpStatus, String code, String description) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.description = description;
    }

    public HttpStatus getHttpStatus() { return this.httpStatus; }
    public String getCode() { return this.code; }
    public String getDescription() { return this.description; }
}
