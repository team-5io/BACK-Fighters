package com.lion._iozoo.document.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record RequestWritingSuggestionsRequest(
        @Schema(description = "제안 대상 원문 (FE가 들고 있는 최신 내용을 그대로 전달)", example = "이 함수는 RACI 배정을 검증합니다.")
        @NotBlank(message = "content는 필수입니다.")
        String content,

        @Schema(description = "커서 위치 주변 맥락 (다음 문단/명확성 제안 기준)", example = "...검증한다. 그 다음으로")
        @NotBlank(message = "cursorContext는 필수입니다.")
        String cursorContext
) {
}
