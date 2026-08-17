package com.lion._iozoo.document.presentation.api.response;

import com.lion._iozoo.document.application.result.DocumentImpactResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record DocumentImpactResponse(
        @Schema(description = "영향받는 문서 ID", example = "200")
        Long documentId,

        @Schema(description = "영향받는 문서 제목", example = "보안 정책 문서")
        String title,

        @Schema(description = "기준 문서로부터의 관계 hop 수 (1이면 직접 연결)", example = "1")
        int depth
) {
    public static DocumentImpactResponse from(DocumentImpactResult result) {
        return DocumentImpactResponse.builder()
                .documentId(result.documentId())
                .title(result.title())
                .depth(result.depth())
                .build();
    }
}
