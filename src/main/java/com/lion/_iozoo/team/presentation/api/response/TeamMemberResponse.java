package com.lion._iozoo.team.presentation.api.response;

import com.lion._iozoo.team.domain.TeamRole;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberEntity;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TeamMemberResponse(
        Long userId,
        TeamRole role,
        LocalDateTime joinedAt
) {
    public static TeamMemberResponse from(TeamMemberEntity teamMember) {
        return TeamMemberResponse.builder()
                .userId(teamMember.getUserId())
                .role(teamMember.getRole())
                .joinedAt(teamMember.getJoinedAt())
                .build();
    }
}
