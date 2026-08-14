package com.lion._iozoo.team.presentation.api;

import com.lion._iozoo.global.presentation.GlobalApiResponse;
import com.lion._iozoo.global.security.AuthUser;
import com.lion._iozoo.team.application.command.CreateTeamCommand;
import com.lion._iozoo.team.application.command.InviteTeamMemberCommand;
import com.lion._iozoo.team.application.usecase.CreateTeamUseCase;
import com.lion._iozoo.team.application.usecase.InviteTeamMemberUseCase;
import com.lion._iozoo.team.application.usecase.ListTeamMembersUseCase;
import com.lion._iozoo.team.application.usecase.RemoveTeamMemberUseCase;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {

    private final CreateTeamUseCase createTeamUseCase;
    private final InviteTeamMemberUseCase inviteTeamMemberUseCase;
    private final RemoveTeamMemberUseCase removeTeamMemberUseCase;
    private final ListTeamMembersUseCase listTeamMembersUseCase;

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

    @DeleteMapping("/{teamId}/members/{memberId}")
    public GlobalApiResponse<Void> removeTeamMember(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long teamId,
            @PathVariable Long memberId) {

        removeTeamMemberUseCase.remove(teamId, authUser.userId(), memberId);

        return GlobalApiResponse.ok(TeamResponseCode.TEAM_MEMBER_REMOVED);
    }

    @GetMapping("/{teamId}/members")
    public GlobalApiResponse<List<TeamMemberResponse>> listTeamMembers(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long teamId) {

        List<TeamMemberResponse> members = listTeamMembersUseCase.listMembers(teamId, authUser.userId())
                .stream()
                .map(TeamMemberResponse::from)
                .toList();

        return GlobalApiResponse.ok(TeamResponseCode.TEAM_MEMBERS_FETCHED, members);
    }
}
