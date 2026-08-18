package com.lion._iozoo.team.application.result;

import com.lion._iozoo.team.domain.TeamRole;
import com.lion._iozoo.team.infrastructure.persistence.TeamEntity;

public record MyTeamResult(TeamEntity team, TeamRole role) {
}
