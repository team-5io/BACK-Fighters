package com.lion._iozoo.docpr.domain.exception;

import com.lion._iozoo.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DocPrErrorCode implements ErrorCode {
    DOCPR_DOCUMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "DOCPR_404_1", "문서를 찾을 수 없습니다."),
    DOCPR_NOT_FOUND(HttpStatus.NOT_FOUND, "DOCPR_404_2", "Doc PR을 찾을 수 없습니다."),
    DOCPR_NOT_DRAFT(HttpStatus.BAD_REQUEST, "DOCPR_400_1", "초안 상태의 문서만 Doc PR로 전환할 수 있습니다."),
    DOCPR_SELF_APPROVAL_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "DOCPR_400_2", "요청자 본인을 승인권자로 지정할 수 없습니다."),
    DOCPR_REQUESTER_NOT_AUTHOR(HttpStatus.FORBIDDEN, "DOCPR_403_1", "문서 작성자만 Doc PR로 전환할 수 있습니다."),
    DOCPR_NOT_APPROVER(HttpStatus.FORBIDDEN, "DOCPR_403_2", "승인권자만 처리할 수 있습니다."),
    DOCPR_ALREADY_TERMINAL(HttpStatus.CONFLICT, "DOCPR_409_1", "이미 승인·반려·병합된 Doc PR입니다."),
    DOCPR_NOT_REQUESTER(HttpStatus.FORBIDDEN, "DOCPR_403_3", "요청자만 재제출할 수 있습니다."),
    DOCPR_NOT_REJECTED(HttpStatus.CONFLICT, "DOCPR_409_2", "반려된 Doc PR만 재제출할 수 있습니다."),
    DOCPR_NOT_APPROVED(HttpStatus.CONFLICT, "DOCPR_409_3", "승인된 Doc PR만 병합할 수 있습니다."),
    DOCPR_ACCESS_DENIED(HttpStatus.FORBIDDEN, "DOCPR_403_4", "해당 Doc PR에 대한 접근 권한이 없습니다."),
    AI_REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "DOCPR_404_3", "AI 리뷰 결과를 찾을 수 없습니다."),
    AI_REVIEW_FAILED(HttpStatus.BAD_GATEWAY, "DOCPR_502_1", "AI 리뷰 요청이 실패했습니다."),
    AI_REVIEW_ISSUE_NOT_FOUND(HttpStatus.NOT_FOUND, "DOCPR_404_4", "AI 리뷰 이슈를 찾을 수 없습니다."),
    AI_REVIEW_ISSUE_ALREADY_PROCESSED(HttpStatus.CONFLICT, "DOCPR_409_4", "이미 처리(해결/건너뛰기)된 이슈입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
