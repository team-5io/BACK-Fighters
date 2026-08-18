package com.lion._iozoo.team.application.usecase;

import com.lion._iozoo.team.infrastructure.persistence.TeamEntity;

import java.util.List;

public interface ListMyTeamsUseCase {
    List<TeamEntity> listMyTeams(Long userId);
}
