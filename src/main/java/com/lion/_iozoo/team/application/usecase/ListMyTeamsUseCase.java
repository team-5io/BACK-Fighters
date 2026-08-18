package com.lion._iozoo.team.application.usecase;

import com.lion._iozoo.team.application.result.MyTeamResult;

import java.util.List;

public interface ListMyTeamsUseCase {
    List<MyTeamResult> listMyTeams(Long userId);
}
