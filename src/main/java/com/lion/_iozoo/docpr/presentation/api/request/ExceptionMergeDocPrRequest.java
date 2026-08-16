package com.lion._iozoo.docpr.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ExceptionMergeDocPrRequest(
        @Schema(description = "예외 병합 사유", example = "긴급 배포 마감으로 사람 리뷰 완료 전 병합")
        @NotBlank(message = "예외 병합 사유는 필수입니다.")
        String reason
) {
}
