package com.lion._iozoo.team.presentation.api.response;

import com.lion._iozoo.team.infrastructure.persistence.TeamEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record TeamResponse(
        @Schema(description = "팀 ID", example = "1")
        Long id,

        @Schema(description = "팀 이름", example = "5조 파이터즈")
        String name
) {
    public static TeamResponse from(TeamEntity team) {
        return TeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .build();
    }
}
