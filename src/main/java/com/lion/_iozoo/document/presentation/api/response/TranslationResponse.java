package com.lion._iozoo.document.presentation.api.response;

import com.lion._iozoo.document.domain.Translation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record TranslationResponse(
        @Schema(description = "번역 결과 ID", example = "1")
        Long id,

        @Schema(description = "문서 ID", example = "100")
        Long documentId,

        @Schema(description = "원문 언어 코드", example = "ko")
        String sourceLanguage,

        @Schema(description = "번역 대상 언어 코드", example = "en")
        String targetLanguage,

        @Schema(description = "번역된 내용 (코드블록·식별자는 원문 보존)", example = "This function validates RACI assignment.")
        String translatedContent,

        @Schema(description = "원문 그대로 보존된 코드 토큰 목록", example = "[\"Doc PR\", \"RACI\"]")
        List<String> preservedTerms,

        @Schema(description = "캐시된 결과를 반환했는지 여부", example = "false")
        boolean cached,

        @Schema(description = "생성 시각", example = "2026-08-18T10:00:00")
        LocalDateTime createdAt
) {
    public static TranslationResponse from(Translation translation, boolean cached) {
        return TranslationResponse.builder()
                .id(translation.getId())
                .documentId(translation.getDocumentId())
                .sourceLanguage(translation.getSourceLanguage())
                .targetLanguage(translation.getTargetLanguage())
                .translatedContent(translation.getTranslatedContent())
                .preservedTerms(translation.getPreservedTerms())
                .cached(cached)
                .createdAt(translation.getCreatedAt())
                .build();
    }
}
