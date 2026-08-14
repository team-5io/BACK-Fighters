package com.lion._iozoo.global.presentation;

import org.springframework.http.HttpStatus;

public record GlobalApiResponse<T>(int status, String code, String message, T data) {

    public static <T> GlobalApiResponse<T> of(HttpStatus httpStatus, String code, String message, T data) {
        return new GlobalApiResponse<>(httpStatus.value(), code, message, data);
    }

    public static GlobalApiResponse<Void> of(HttpStatus httpStatus, String code, String message) {
        return of(httpStatus, code, message, null);
    }

    public static <T> GlobalApiResponse<T> ok(String code, String message, T data) {
        return of(HttpStatus.OK, code, message, data);
    }

    public static GlobalApiResponse<Void> ok(String code, String message) {
        return of(HttpStatus.OK, code, message);
    }

    public static <T> GlobalApiResponse<T> ok(ResponseCode code, T data) {
        return ok(code.getCode(), code.getMessage(), data);
    }

    public static GlobalApiResponse<Void> ok(ResponseCode code) {
        return ok(code.getCode(), code.getMessage());
    }

    public static <T> GlobalApiResponse<T> created(String code, String message, T data) {
        return of(HttpStatus.CREATED, code, message, data);
    }

    public static <T> GlobalApiResponse<T> created(ResponseCode code, T data) {
        return created(code.getCode(), code.getMessage(), data);
    }
}