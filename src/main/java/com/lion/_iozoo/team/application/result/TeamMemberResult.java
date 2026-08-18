package com.lion._iozoo.team.application.result;

import com.lion._iozoo.team.domain.TeamRole;

import java.time.LocalDateTime;

public record TeamMemberResult(Long memberId, TeamRole role, LocalDateTime joinedAt, String name, String email) {
}
