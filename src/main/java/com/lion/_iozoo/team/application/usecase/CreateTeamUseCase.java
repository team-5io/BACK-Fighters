package com.lion._iozoo.team.application.usecase;

import com.lion._iozoo.team.application.command.CreateTeamCommand;
import com.lion._iozoo.team.infrastructure.persistence.TeamEntity;

public interface CreateTeamUseCase {
    TeamEntity createTeam(Long creatorUserId, CreateTeamCommand command);
}
