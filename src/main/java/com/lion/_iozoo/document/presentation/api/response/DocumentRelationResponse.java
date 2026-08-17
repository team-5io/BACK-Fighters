package com.lion._iozoo.document.presentation.api.response;

import com.lion._iozoo.document.domain.DocumentRelation;
import com.lion._iozoo.document.domain.RelationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DocumentRelationResponse(
        @Schema(description = "관계 ID", example = "1")
        Long id,

        @Schema(description = "관계의 출발 문서(source) ID", example = "100")
        Long sourceDocumentId,

        @Schema(description = "관계의 대상 문서(target) ID", example = "200")
        Long targetDocumentId,

        @Schema(description = "관계 유형", example = "REFERENCE")
        RelationType relationType,

        @Schema(description = "생성 시각")
        LocalDateTime createdAt
) {
    public static DocumentRelationResponse from(DocumentRelation documentRelation) {
        return DocumentRelationResponse.builder()
                .id(documentRelation.getId())
                .sourceDocumentId(documentRelation.getSourceDocumentId())
                .targetDocumentId(documentRelation.getTargetDocumentId())
                .relationType(documentRelation.getRelationType())
                .createdAt(documentRelation.getCreatedAt())
                .build();
    }
}
