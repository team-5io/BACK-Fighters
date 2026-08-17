package com.lion._iozoo.team.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UpsertCollaborationRuleRequest(
        @Schema(description = "협업 규칙 내용", example = "1. 초안은 작성 후 24시간 내 리뷰어에게 공유한다.\n2. 리뷰 피드백은 48시간 내 응답한다.")
        @NotBlank(message = "협업 규칙 내용은 필수입니다.")
        String content
) {
}
