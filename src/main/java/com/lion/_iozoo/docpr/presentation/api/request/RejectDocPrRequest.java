package com.lion._iozoo.docpr.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record RejectDocPrRequest(
        @Schema(description = "반려 사유", example = "결제 정책 변경 내용이 최신 기획과 어긋남")
        @NotBlank(message = "반려 사유는 필수입니다.")
        String reason
) {
}
