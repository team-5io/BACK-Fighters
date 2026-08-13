package com.lion._iozoo.global.presentation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion._iozoo.global.exception.ApplicationException;
import com.lion._iozoo.global.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.Map;

public record GlobalApiErrorResponse(
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime timestamp,
        int status,
        String code,
        String message,
        String traceId,
        Map<String, Object> details) {

    public static GlobalApiErrorResponse of(ApplicationException e, String traceId) {
        return of(e.getErrorCode(), e.getMessage(), traceId, e.getContext());
    }

    public static GlobalApiErrorResponse of(ErrorCode errorCode, String traceId) {
        return of(errorCode, errorCode.getMessage(), traceId, Map.of());
    }

    public static GlobalApiErrorResponse of(ErrorCode errorCode, String traceId, Map<String, Object> details) {
        return of(errorCode, errorCode.getMessage(), traceId, details);
    }

    public static GlobalApiErrorResponse of(ErrorCode errorCode, String message, String traceId, Map<String, Object> details) {
        return new GlobalApiErrorResponse(
                LocalDateTime.now(),
                errorCode.getHttpStatus().value(),
                errorCode.getCode(),
                message,
                traceId,
                details == null ? Map.of() : Map.copyOf(details));
    }
}
