package com.lion._iozoo.docpr.domain.exception;

import com.lion._iozoo.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DocPrErrorCode implements ErrorCode {
    DOCPR_DOCUMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "DOCPR_404_1", "문서를 찾을 수 없습니다."),
    DOCPR_NOT_DRAFT(HttpStatus.BAD_REQUEST, "DOCPR_400_1", "초안 상태의 문서만 Doc PR로 전환할 수 있습니다."),
    DOCPR_SELF_APPROVAL_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "DOCPR_400_2", "요청자 본인을 승인권자로 지정할 수 없습니다."),
    DOCPR_REQUESTER_NOT_AUTHOR(HttpStatus.FORBIDDEN, "DOCPR_403_1", "문서 작성자만 Doc PR로 전환할 수 있습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
