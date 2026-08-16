package com.lion._iozoo.document.domain.exception;

import com.lion._iozoo.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DocumentErrorCode implements ErrorCode {
    DOCUMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "DOCUMENT_404_1", "문서를 찾을 수 없습니다."),
    DOCUMENT_NOT_DRAFT(HttpStatus.BAD_REQUEST, "DOCUMENT_400_1", "초안 상태의 문서만 편집할 수 있습니다."),
    DOCUMENT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "DOCUMENT_403_1", "해당 문서에 대한 접근 권한이 없습니다."),
    DOCUMENT_RACI_DUPLICATE_USER(HttpStatus.BAD_REQUEST, "DOCUMENT_400_2", "한 요청에 같은 사용자를 중복해서 지정할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
