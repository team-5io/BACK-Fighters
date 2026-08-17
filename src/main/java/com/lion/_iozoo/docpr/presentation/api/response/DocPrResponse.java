package com.lion._iozoo.docpr.presentation.api.response;

import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.domain.DocPrStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DocPrResponse(
        @Schema(description = "Doc PR ID", example = "1")
        Long id,

        @Schema(description = "대상 문서 ID", example = "100")
        Long documentId,

        @Schema(description = "요청자(작성자, R) 사용자 ID", example = "10")
        Long requesterId,

        @Schema(description = "승인권자(A) 사용자 ID", example = "2")
        Long approverId,

        @Schema(description = "이 Doc PR이 제안하는 변경 내용", example = "3장 배포 절차를 최신 CI 스크립트 기준으로 수정")
        String proposedContent,

        @Schema(description = "Doc PR 상태", example = "CREATED")
        DocPrStatus status,

        @Schema(description = "병합 확정 시각 (미병합이면 null)", example = "2026-08-16T21:00:00")
        LocalDateTime mergedAt,

        @Schema(description = "차단 조건을 무시하고 예외적으로 병합됐는지 여부", example = "false")
        boolean exceptionMerge,

        @Schema(description = "예외 병합 사유 (예외 병합이 아니면 null)", example = "긴급 배포 마감으로 사람 리뷰 완료 전 병합")
        String exceptionReason
) {
    public static DocPrResponse from(DocPr docPr) {
        return DocPrResponse.builder()
                .id(docPr.getId())
                .documentId(docPr.getDocumentId())
                .requesterId(docPr.getRequesterId())
                .approverId(docPr.getApproverId())
                .proposedContent(docPr.getProposedContent())
                .status(docPr.getStatus())
                .mergedAt(docPr.getMergedAt())
                .exceptionMerge(docPr.isExceptionMerge())
                .exceptionReason(docPr.getExceptionReason())
                .build();
    }
}
