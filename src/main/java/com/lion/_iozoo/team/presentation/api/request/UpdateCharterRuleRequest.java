package com.lion._iozoo.team.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UpdateCharterRuleRequest(
        @Schema(description = "규칙 제목", example = "리뷰 SLA")
        @NotBlank(message = "제목은 필수입니다.")
        String title,

        @Schema(description = "규칙 내용", example = "초안은 작성 후 24시간 내 리뷰어에게 공유한다.")
        @NotBlank(message = "내용은 필수입니다.")
        String content
) {
}
