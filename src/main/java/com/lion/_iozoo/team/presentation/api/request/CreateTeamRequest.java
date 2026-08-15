package com.lion._iozoo.team.presentation.api.request;

import jakarta.validation.constraints.NotBlank;

public record CreateTeamRequest(
        @NotBlank(message = "팀 이름은 필수입니다.")
        String name
) {
}
