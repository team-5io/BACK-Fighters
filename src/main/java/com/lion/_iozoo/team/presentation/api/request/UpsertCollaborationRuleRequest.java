package com.lion._iozoo.team.presentation.api.request;

import com.lion._iozoo.team.domain.CollaborationRuleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpsertCollaborationRuleRequest(
        @Schema(description = "협업 규칙 내용", example = "1. 초안은 작성 후 24시간 내 리뷰어에게 공유한다.\n2. 리뷰 피드백은 48시간 내 응답한다.")
        @NotBlank(message = "협업 규칙 내용은 필수입니다.")
        String content,

        @Schema(description = "상태 (DRAFT: 초안, ADOPTED: 채택 확정)", example = "DRAFT")
        @NotNull(message = "상태는 필수입니다.")
        CollaborationRuleStatus status
) {
}
