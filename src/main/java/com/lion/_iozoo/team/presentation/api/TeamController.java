package com.lion._iozoo.team.presentation.api;

import com.lion._iozoo.global.presentation.GlobalApiResponse;
import com.lion._iozoo.global.security.AuthUser;
import com.lion._iozoo.team.application.command.CreateTeamCommand;
import com.lion._iozoo.team.application.command.InviteTeamMemberCommand;
import com.lion._iozoo.team.application.usecase.CreateTeamUseCase;
import com.lion._iozoo.team.application.usecase.InviteTeamMemberUseCase;
import com.lion._iozoo.team.infrastructure.persistence.TeamEntity;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberEntity;
import com.lion._iozoo.team.presentation.api.common.TeamResponseCode;
import com.lion._iozoo.team.presentation.api.request.CreateTeamRequest;
import com.lion._iozoo.team.presentation.api.request.InviteTeamMemberRequest;
import com.lion._iozoo.team.presentation.api.response.TeamMemberResponse;
import com.lion._iozoo.team.presentation.api.response.TeamResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {

    private final CreateTeamUseCase createTeamUseCase;
    private final InviteTeamMemberUseCase inviteTeamMemberUseCase;

    @PostMapping
    public GlobalApiResponse<TeamResponse> createTeam(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody @Valid CreateTeamRequest request) {

        CreateTeamCommand command = new CreateTeamCommand(request.name());
        TeamEntity team = createTeamUseCase.createTeam(authUser.userId(), command);

        return GlobalApiResponse.created(TeamResponseCode.TEAM_CREATED, TeamResponse.from(team));
    }

    @PostMapping("/{teamId}/invitations")
    public GlobalApiResponse<TeamMemberResponse> inviteTeamMember(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long teamId,
            @RequestBody @Valid InviteTeamMemberRequest request) {

        InviteTeamMemberCommand command = new InviteTeamMemberCommand(request.email());
        TeamMemberEntity teamMember = inviteTeamMemberUseCase.invite(teamId, authUser.userId(), command);

        return GlobalApiResponse.created(TeamResponseCode.TEAM_MEMBER_INVITED, TeamMemberResponse.from(teamMember));
    }
}
