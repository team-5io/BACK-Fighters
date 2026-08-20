package com.lion._iozoo.team.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AdoptCharterRulesRequest(
        @Schema(description = "채택할 규칙 ID 목록", example = "[1, 2]")
        @NotEmpty(message = "채택할 규칙 ID는 1개 이상 필요합니다.")
        List<Long> ruleIds
) {
}
