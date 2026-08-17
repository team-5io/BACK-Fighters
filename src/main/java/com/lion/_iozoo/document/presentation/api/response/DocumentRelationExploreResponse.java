package com.lion._iozoo.document.presentation.api.response;

import com.lion._iozoo.document.application.result.DocumentRelationExploreResult;
import com.lion._iozoo.document.domain.RelationDirection;
import com.lion._iozoo.document.domain.RelationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DocumentRelationExploreResponse(
        @Schema(description = "관계 ID", example = "1")
        Long relationId,

        @Schema(description = "조회 기준 문서 대비 방향 (OUTGOING: 기준 문서가 source, INCOMING: 기준 문서가 target)", example = "OUTGOING")
        RelationDirection direction,

        @Schema(description = "관계 유형", example = "REFERENCE")
        RelationType relationType,

        @Schema(description = "이웃 문서(반대편) ID", example = "200")
        Long neighborDocumentId,

        @Schema(description = "이웃 문서 제목", example = "보안 정책 문서")
        String neighborTitle,

        @Schema(description = "관계 생성 시각")
        LocalDateTime createdAt
) {
    public static DocumentRelationExploreResponse from(DocumentRelationExploreResult result) {
        return DocumentRelationExploreResponse.builder()
                .relationId(result.relationId())
                .direction(result.direction())
                .relationType(result.relationType())
                .neighborDocumentId(result.neighborDocumentId())
                .neighborTitle(result.neighborTitle())
                .createdAt(result.createdAt())
                .build();
    }
}
