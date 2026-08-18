package com.lion._iozoo.document.presentation.api.response;

import com.lion._iozoo.document.application.result.MyDocumentPermissionResult;
import com.lion._iozoo.document.domain.DocumentAccessLevel;
import com.lion._iozoo.document.domain.RaciRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record MyDocumentPermissionResponse(
        @Schema(description = "문서 ID", example = "100")
        Long documentId,

        @Schema(description = "이 문서에 대한 내 RACI 역할 (없으면 null)", example = "C")
        RaciRole role,

        @Schema(description = "접근수준 (FULL: 전체 열람, OFFICIAL_ONLY: 공식 문서만 열람, NONE: 열람 불가)", example = "FULL")
        DocumentAccessLevel accessLevel,

        @Schema(description = "문서 작성자(R) 본인 여부", example = "false")
        boolean isAuthor,

        @Schema(description = "Doc PR·검토 근거 조회 가능 여부", example = "true")
        boolean canViewDocPr
) {
    public static MyDocumentPermissionResponse from(MyDocumentPermissionResult result) {
        return MyDocumentPermissionResponse.builder()
                .documentId(result.documentId())
                .role(result.role())
                .accessLevel(result.accessLevel())
                .isAuthor(result.isAuthor())
                .canViewDocPr(result.canViewDocPr())
                .build();
    }
}
