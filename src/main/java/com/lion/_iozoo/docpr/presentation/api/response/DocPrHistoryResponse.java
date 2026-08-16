package com.lion._iozoo.docpr.presentation.api.response;

import com.lion._iozoo.docpr.application.result.DocPrHistoryEntry;
import com.lion._iozoo.docpr.domain.DocPrStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DocPrHistoryResponse(
        @Schema(description = "전이 이전 상태 (최초 생성이면 null)", example = "HUMAN_REVIEW")
        DocPrStatus fromStatus,

        @Schema(description = "전이 이후 상태", example = "APPROVED")
        DocPrStatus toStatus,

        @Schema(description = "상태를 변경한 사용자 ID", example = "2")
        Long actorId,

        @Schema(description = "사유 (없으면 null)", example = "결제 정책 문구 수정 필요")
        String reason,

        @Schema(description = "전이 시각", example = "2026-08-16T21:00:00")
        LocalDateTime createdAt
) {
    public static DocPrHistoryResponse from(DocPrHistoryEntry entry) {
        return DocPrHistoryResponse.builder()
                .fromStatus(entry.fromStatus())
                .toStatus(entry.toStatus())
                .actorId(entry.actorId())
                .reason(entry.reason())
                .createdAt(entry.createdAt())
                .build();
    }
}
