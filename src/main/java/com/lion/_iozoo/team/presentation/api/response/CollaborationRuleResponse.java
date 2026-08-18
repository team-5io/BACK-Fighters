package com.lion._iozoo.team.presentation.api.response;

import com.lion._iozoo.team.domain.CollaborationRuleStatus;
import com.lion._iozoo.team.infrastructure.persistence.TeamCollaborationRuleEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CollaborationRuleResponse(
        @Schema(description = "협업 규칙 ID", example = "1")
        Long id,

        @Schema(description = "팀 ID", example = "1")
        Long teamId,

        @Schema(description = "협업 규칙 내용", example = "1. 초안은 작성 후 24시간 내 리뷰어에게 공유한다.")
        String content,

        @Schema(description = "상태 (DRAFT: 초안, ADOPTED: 채택 확정)", example = "DRAFT")
        CollaborationRuleStatus status,

        @Schema(description = "생성 시각", example = "2026-08-17T10:00:00")
        LocalDateTime createdAt,

        @Schema(description = "마지막 수정 시각", example = "2026-08-17T10:00:00")
        LocalDateTime updatedAt
) {
    public static CollaborationRuleResponse from(TeamCollaborationRuleEntity entity) {
        return CollaborationRuleResponse.builder()
                .id(entity.getId())
                .teamId(entity.getTeamId())
                .content(entity.getContent())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
