package com.lion._iozoo.global.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public abstract class ApplicationException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Map<String, Object> context = new HashMap<>();

    protected ApplicationException(ErrorCode errorCode) {
        this(errorCode, errorCode.getMessage());
    }

    protected ApplicationException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    protected ApplicationException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    protected ApplicationException(ErrorCode errorCode, String message, Map<String, Object> context) {
        super(message);
        this.errorCode = errorCode;
        if (context != null) {
            this.context.putAll(context);
        }
    }

    protected void addContext(String key, Object value) {
        context.put(key, value);
    }

    public Map<String, Object> getContext() {
        return Map.copyOf(context);
    }
}
