package com.lion._iozoo.docpr.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AddDocPrReviewRequest(
        @Schema(description = "리뷰 의견", example = "결제 정책 문구가 명확해서 승인 가능해 보입니다.")
        @NotBlank(message = "리뷰 의견은 필수입니다.")
        String comment
) {
}
