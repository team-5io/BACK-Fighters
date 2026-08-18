package com.lion._iozoo.document.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record RequestTranslationRequest(
        @Schema(description = "번역 대상 블록 id (FE 에디터가 생성하는 블록 id, 블록 하나당 한 번씩 호출)", example = "block-abc123")
        @NotBlank(message = "blockId는 필수입니다.")
        String blockId,

        @Schema(description = "번역할 원문 (FE가 들고 있는 최신 내용을 그대로 전달)", example = "이 함수는 RACI 배정을 검증합니다.")
        @NotBlank(message = "content는 필수입니다.")
        String content,

        @Schema(description = "원문 언어 코드", example = "ko")
        @NotBlank(message = "sourceLanguage는 필수입니다.")
        String sourceLanguage,

        @Schema(description = "번역 대상 언어 코드", example = "en")
        @NotBlank(message = "targetLanguage는 필수입니다.")
        String targetLanguage
) {
}
