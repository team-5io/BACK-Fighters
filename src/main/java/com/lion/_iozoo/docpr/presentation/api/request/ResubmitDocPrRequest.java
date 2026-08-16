package com.lion._iozoo.docpr.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ResubmitDocPrRequest(
        @Schema(description = "수정된 제안 내용", example = "반려 사유를 반영해 결제 정책 문구를 수정함")
        @NotBlank(message = "제안 내용은 필수입니다.")
        String proposedContent
) {
}
