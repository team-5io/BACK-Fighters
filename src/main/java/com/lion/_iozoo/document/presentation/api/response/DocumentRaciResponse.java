package com.lion._iozoo.document.presentation.api.response;

import com.lion._iozoo.document.application.result.DocumentRaciEntry;
import com.lion._iozoo.document.domain.RaciRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DocumentRaciResponse(
        @Schema(description = "배정된 사용자 ID", example = "3")
        Long userId,

        @Schema(description = "RACI 역할", example = "R")
        RaciRole role,

        @Schema(description = "배정한 팀 관리자 사용자 ID", example = "1")
        Long assignedBy,

        @Schema(description = "배정 시각", example = "2026-08-17T10:00:00")
        LocalDateTime assignedAt
) {
    public static DocumentRaciResponse from(DocumentRaciEntry entry) {
        return DocumentRaciResponse.builder()
                .userId(entry.userId())
                .role(entry.role())
                .assignedBy(entry.assignedBy())
                .assignedAt(entry.assignedAt())
                .build();
    }
}
