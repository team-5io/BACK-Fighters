package com.lion._iozoo.document.presentation.api.request;

import com.lion._iozoo.document.domain.RelationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CreateDocumentRelationRequest(
        @Schema(description = "관계를 맺을 대상 문서 ID", example = "200")
        @NotNull(message = "대상 문서 ID는 필수입니다.")
        Long targetDocumentId,

        @Schema(description = "관계 유형", example = "REFERENCE")
        @NotNull(message = "관계 유형은 필수입니다.")
        RelationType relationType
) {
}
