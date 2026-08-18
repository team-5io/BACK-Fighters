package com.lion._iozoo.team.presentation.api.response;

import com.lion._iozoo.team.application.result.TeamMemberResult;
import com.lion._iozoo.team.domain.TeamRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TeamMemberResponse(
        @Schema(description = "팀원 멤버십 ID (team_members 테이블 PK)", example = "1")
        Long memberId,

        @Schema(description = "이름", example = "김재원")
        String name,

        @Schema(description = "이메일", example = "user@example.com")
        String email,

        @Schema(description = "팀 내 역할", example = "MEMBER")
        TeamRole role,

        @Schema(description = "팀 합류 시각", example = "2026-08-14T10:00:00")
        LocalDateTime joinedAt
) {
    public static TeamMemberResponse from(TeamMemberResult result) {
        return TeamMemberResponse.builder()
                .memberId(result.memberId())
                .name(result.name())
                .email(result.email())
                .role(result.role())
                .joinedAt(result.joinedAt())
                .build();
    }
}
