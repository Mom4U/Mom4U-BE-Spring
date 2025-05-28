package com.hanium.mom4u.global.exception;

import com.hanium.mom4u.global.response.StatusCode;

public class CustomException extends RuntimeException {

    private final StatusCode statusCode;

    public CustomException(StatusCode statusCode) {
        super(statusCode.getDescription());
        this.statusCode = statusCode;
    }

    public static CustomException of(StatusCode statusCode) {
        return new CustomException(statusCode);
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }
}
