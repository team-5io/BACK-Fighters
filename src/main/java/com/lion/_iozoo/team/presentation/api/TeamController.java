package com.lion._iozoo.team.presentation.api;

import com.lion._iozoo.global.presentation.GlobalApiResponse;
import com.lion._iozoo.global.security.AuthUser;
import com.lion._iozoo.team.application.command.CreateTeamCommand;
import com.lion._iozoo.team.application.usecase.CreateTeamUseCase;
import com.lion._iozoo.team.infrastructure.persistence.TeamEntity;
import com.lion._iozoo.team.presentation.api.common.TeamResponseCode;
import com.lion._iozoo.team.presentation.api.request.CreateTeamRequest;
import com.lion._iozoo.team.presentation.api.response.TeamResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {

    private final CreateTeamUseCase createTeamUseCase;

    @PostMapping
    public GlobalApiResponse<TeamResponse> createTeam(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody @Valid CreateTeamRequest request) {

        CreateTeamCommand command = new CreateTeamCommand(request.name());
        TeamEntity team = createTeamUseCase.createTeam(authUser.userId(), command);

        return GlobalApiResponse.created(TeamResponseCode.TEAM_CREATED, TeamResponse.from(team));
    }
}
