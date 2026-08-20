package com.lion._iozoo.document.presentation.api.response;

import com.lion._iozoo.document.application.result.DocumentRelationExploreResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record DocumentWithRelationsResponse(
        @Schema(description = "문서 정보 (문서 목록 조회와 동일한 구조)")
        DocumentResponse document,

        @Schema(description = "이 문서와 연결된 관계 목록. 연결된 관계가 하나도 없는 독립 문서는 null (빈 배열이 아님)")
        List<DocumentRelationExploreResponse> relations
) {
    public static DocumentWithRelationsResponse of(DocumentResponse document, List<DocumentRelationExploreResult> relations) {
        return DocumentWithRelationsResponse.builder()
                .document(document)
                .relations(relations == null ? null : relations.stream().map(DocumentRelationExploreResponse::from).toList())
                .build();
    }
}
