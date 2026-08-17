package com.lion._iozoo.team.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateTeamRequest(
        @Schema(description = "팀 이름", example = "5조 파이터즈")
        @NotBlank(message = "팀 이름은 필수입니다.")
        String name
) {
}
