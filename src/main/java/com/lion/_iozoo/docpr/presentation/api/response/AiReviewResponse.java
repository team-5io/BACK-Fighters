package com.lion._iozoo.docpr.presentation.api.response;

import com.lion._iozoo.docpr.domain.AiReview;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AiReviewResponse(
        @Schema(description = "Doc PR ID", example = "7")
        Long docPrId,

        @Schema(description = "연결 문서와의 상충 여부", example = "false")
        boolean hasConflict,

        @Schema(description = "기존 결정과의 정합성", example = "true")
        boolean isConsistent,

        @Schema(description = "협업 규칙(Charter) 위반 여부", example = "false")
        boolean violatesCharter,

        @Schema(description = "검토 근거", example = "string")
        String evidence,

        @Schema(description = "검토 시각", example = "2026-08-19T10:00:00")
        LocalDateTime reviewedAt
) {
    public static AiReviewResponse from(AiReview review) {
        return AiReviewResponse.builder()
                .docPrId(review.getDocPrId())
                .hasConflict(review.isHasConflict())
                .isConsistent(review.isConsistent())
                .violatesCharter(review.isViolatesCharter())
                .evidence(review.getEvidence())
                .reviewedAt(review.getReviewedAt())
                .build();
    }
}
