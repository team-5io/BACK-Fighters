package com.lion._iozoo.team.presentation.api.response;

import com.lion._iozoo.team.domain.CharterRuleStatus;
import com.lion._iozoo.team.infrastructure.persistence.CharterRuleEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CharterRuleResponse(
        @Schema(description = "규칙 ID", example = "1")
        Long id,

        @Schema(description = "팀 ID", example = "1")
        Long teamId,

        @Schema(description = "규칙 제목", example = "리뷰 SLA")
        String title,

        @Schema(description = "규칙 내용", example = "초안은 작성 후 24시간 내 리뷰어에게 공유한다.")
        String content,

        @Schema(description = "상태 (DRAFT: 초안, ADOPTED: 채택 확정)", example = "DRAFT")
        CharterRuleStatus status,

        @Schema(description = "생성 시각", example = "2026-08-21T10:00:00")
        LocalDateTime createdAt,

        @Schema(description = "마지막 수정 시각", example = "2026-08-21T10:00:00")
        LocalDateTime updatedAt
) {
    public static CharterRuleResponse from(CharterRuleEntity rule) {
        return CharterRuleResponse.builder()
                .id(rule.getId())
                .teamId(rule.getTeamId())
                .title(rule.getTitle())
                .content(rule.getContent())
                .status(rule.getStatus())
                .createdAt(rule.getCreatedAt())
                .updatedAt(rule.getUpdatedAt())
                .build();
    }
}
