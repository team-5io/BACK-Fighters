package com.lion._iozoo.team.presentation.api.response;

import com.lion._iozoo.team.domain.TeamRole;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TeamMemberResponse(
        @Schema(description = "팀원 사용자 ID", example = "10")
        Long userId,

        @Schema(description = "팀 내 역할", example = "MEMBER")
        TeamRole role,

        @Schema(description = "팀 합류 시각", example = "2026-08-14T10:00:00")
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
