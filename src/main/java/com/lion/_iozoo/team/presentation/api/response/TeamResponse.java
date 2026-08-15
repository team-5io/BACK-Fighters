package com.lion._iozoo.team.presentation.api.response;

import com.lion._iozoo.team.infrastructure.persistence.TeamEntity;
import lombok.Builder;

@Builder
public record TeamResponse(
        Long id,
        String name
) {
    public static TeamResponse from(TeamEntity team) {
        return TeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .build();
    }
}
