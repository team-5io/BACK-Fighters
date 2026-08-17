package com.lion._iozoo.docpr.presentation.api.response;

import com.lion._iozoo.docpr.application.result.DocPrReview;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DocPrReviewResponse(
        @Schema(description = "리뷰 ID", example = "1")
        Long id,

        @Schema(description = "대상 Doc PR ID", example = "1")
        Long docPrId,

        @Schema(description = "리뷰어(C 또는 A) 사용자 ID", example = "5")
        Long reviewerId,

        @Schema(description = "리뷰 의견", example = "결제 정책 문구가 명확해서 승인 가능해 보입니다.")
        String comment,

        @Schema(description = "등록 시각", example = "2026-08-16T21:00:00")
        LocalDateTime createdAt
) {
    public static DocPrReviewResponse from(DocPrReview review) {
        return DocPrReviewResponse.builder()
                .id(review.id())
                .docPrId(review.docPrId())
                .reviewerId(review.reviewerId())
                .comment(review.comment())
                .createdAt(review.createdAt())
                .build();
    }
}
