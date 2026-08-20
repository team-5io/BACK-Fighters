package com.lion._iozoo.team.presentation.api.response;

import com.lion._iozoo.team.application.result.CharterRule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record CharterRuleResponse(
        @Schema(description = "규칙 ID (AI-Fighters 발급 uuid)", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        String id,

        @Schema(description = "상태 (draft/adopted/archived, AI-Fighters가 내려주는 값 그대로)", example = "draft")
        String status,

        @Schema(description = "규칙 제목", example = "리뷰 SLA")
        String title,

        @Schema(description = "규칙 내용", example = "초안은 작성 후 24시간 내 리뷰어에게 공유한다.")
        String content
) {
    public static CharterRuleResponse from(CharterRule rule) {
        return CharterRuleResponse.builder()
                .id(rule.id())
                .status(rule.status())
                .title(rule.title())
                .content(rule.content())
                .build();
    }
}
