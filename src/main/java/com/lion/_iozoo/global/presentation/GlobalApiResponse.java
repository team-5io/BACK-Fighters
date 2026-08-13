package com.lion._iozoo.global.presentation;

import lombok.Getter;

@Getter
public class GlobalApiResponse<T> {
    private final int status;
    private final String message;
    private final T data;

    private GlobalApiResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // 성공 응답 (데이터 있음)
    public static <T> GlobalApiResponse<T> ok(T data) {
        return new GlobalApiResponse<>(200, "SUCCESS", data);
    }

    // 성공 응답 (데이터 없음)
    public static <T> GlobalApiResponse<T> ok() {
        return new GlobalApiResponse<>(200, "SUCCESS", null);
    }

    // 생성 성공 응답
    public static <T> GlobalApiResponse<T> created(T data) {
        return new GlobalApiResponse<>(201, "CREATED", data);
    }
}