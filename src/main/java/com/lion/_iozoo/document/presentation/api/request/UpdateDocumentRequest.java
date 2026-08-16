package com.lion._iozoo.document.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UpdateDocumentRequest(
        @Schema(description = "문서 제목", example = "온보딩 가이드 (개정)")
        @NotBlank(message = "제목은 필수입니다.")
        String title,

        @Schema(description = "문서 내용", example = "수정된 본문 내용...")
        String content
) {
}
