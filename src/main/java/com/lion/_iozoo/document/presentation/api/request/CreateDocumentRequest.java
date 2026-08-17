package com.lion._iozoo.document.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateDocumentRequest(
        @Schema(description = "문서가 속할 팀 ID", example = "1")
        @NotNull(message = "팀 ID는 필수입니다.")
        Long teamId,

        @Schema(description = "문서 제목", example = "온보딩 가이드")
        @NotBlank(message = "제목은 필수입니다.")
        String title,

        @Schema(description = "문서 내용", example = "본문 내용...")
        String content
) {
}
