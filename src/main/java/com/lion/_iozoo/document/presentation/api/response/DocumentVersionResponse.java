package com.lion._iozoo.document.presentation.api.response;

import com.lion._iozoo.document.domain.DocumentVersion;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DocumentVersionResponse(
        @Schema(description = "버전 ID", example = "1")
        Long id,

        @Schema(description = "버전 번호 (1부터 순차 증가)", example = "2")
        int versionNo,

        @Schema(description = "해당 버전 시점의 문서 내용 스냅샷", example = "변경된 문서 내용")
        String content,

        @Schema(description = "이 버전을 만든 Doc PR ID (최초 버전은 null)", example = "5")
        Long docPrId,

        @Schema(description = "버전 생성 시각")
        LocalDateTime createdAt
) {
    public static DocumentVersionResponse from(DocumentVersion documentVersion) {
        return DocumentVersionResponse.builder()
                .id(documentVersion.getId())
                .versionNo(documentVersion.getVersionNo())
                .content(documentVersion.getContent())
                .docPrId(documentVersion.getDocPrId())
                .createdAt(documentVersion.getCreatedAt())
                .build();
    }
}
