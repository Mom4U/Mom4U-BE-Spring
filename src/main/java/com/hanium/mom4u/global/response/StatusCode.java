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
