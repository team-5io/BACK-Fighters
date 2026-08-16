package com.lion._iozoo.docpr.presentation.api.response;

import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.domain.DocPrStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

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
        DocPrStatus status
) {
    public static DocPrResponse from(DocPr docPr) {
        return DocPrResponse.builder()
                .id(docPr.getId())
                .documentId(docPr.getDocumentId())
                .requesterId(docPr.getRequesterId())
                .approverId(docPr.getApproverId())
                .proposedContent(docPr.getProposedContent())
                .status(docPr.getStatus())
                .build();
    }
}
