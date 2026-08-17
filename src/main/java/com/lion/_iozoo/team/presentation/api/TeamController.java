package com.lion._iozoo.team.presentation.api;

import com.lion._iozoo.global.presentation.GlobalApiResponse;
import com.lion._iozoo.global.security.AuthUser;
import com.lion._iozoo.team.application.command.CreateTeamCommand;
import com.lion._iozoo.team.application.command.InviteTeamMemberCommand;
import com.lion._iozoo.team.application.command.UpsertCollaborationRuleCommand;
import com.lion._iozoo.team.application.usecase.AdoptCollaborationRuleUseCase;
import com.lion._iozoo.team.application.usecase.CreateTeamUseCase;
import com.lion._iozoo.team.application.usecase.GetCollaborationRuleUseCase;
import com.lion._iozoo.team.application.usecase.InviteTeamMemberUseCase;
import com.lion._iozoo.team.application.usecase.ListTeamMembersUseCase;
import com.lion._iozoo.team.application.usecase.RemoveTeamMemberUseCase;
import com.lion._iozoo.team.application.usecase.UpsertCollaborationRuleUseCase;
import com.lion._iozoo.team.infrastructure.persistence.TeamCollaborationRuleEntity;
import com.lion._iozoo.team.infrastructure.persistence.TeamEntity;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberEntity;
import com.lion._iozoo.team.presentation.api.common.TeamResponseCode;
import com.lion._iozoo.team.presentation.api.request.CreateTeamRequest;
import com.lion._iozoo.team.presentation.api.request.InviteTeamMemberRequest;
import com.lion._iozoo.team.presentation.api.request.UpsertCollaborationRuleRequest;
import com.lion._iozoo.team.presentation.api.response.CollaborationRuleResponse;
import com.lion._iozoo.team.presentation.api.response.TeamMemberResponse;
import com.lion._iozoo.team.presentation.api.response.TeamResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Team", description = "팀 생성/팀원 관리 API")
@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {

    private final CreateTeamUseCase createTeamUseCase;
    private final InviteTeamMemberUseCase inviteTeamMemberUseCase;
    private final RemoveTeamMemberUseCase removeTeamMemberUseCase;
    private final ListTeamMembersUseCase listTeamMembersUseCase;
    private final GetCollaborationRuleUseCase getCollaborationRuleUseCase;
    private final UpsertCollaborationRuleUseCase upsertCollaborationRuleUseCase;
    private final AdoptCollaborationRuleUseCase adoptCollaborationRuleUseCase;

    @Operation(summary = "팀 생성", description = "새 협업 팀 공간을 생성한다. 생성자는 자동으로 ADMIN이 된다.")
    @PostMapping
    public GlobalApiResponse<TeamResponse> createTeam(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody @Valid CreateTeamRequest request) {

        CreateTeamCommand command = new CreateTeamCommand(request.name());
        TeamEntity team = createTeamUseCase.createTeam(authUser.userId(), command);

        return GlobalApiResponse.created(TeamResponseCode.TEAM_CREATED, TeamResponse.from(team));
    }

    @Operation(summary = "팀원 초대", description = "ADMIN이 이미 가입된 유저를 이메일로 팀에 초대해 MEMBER로 등록한다.")
    @PostMapping("/{teamId}/invitations")
    public GlobalApiResponse<TeamMemberResponse> inviteTeamMember(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long teamId,
            @RequestBody @Valid InviteTeamMemberRequest request) {

        InviteTeamMemberCommand command = new InviteTeamMemberCommand(request.email());
        TeamMemberEntity teamMember = inviteTeamMemberUseCase.invite(teamId, authUser.userId(), command);

        return GlobalApiResponse.created(TeamResponseCode.TEAM_MEMBER_INVITED, TeamMemberResponse.from(teamMember));
    }

    @Operation(summary = "팀원 추방/탈퇴", description = "ADMIN이 팀원을 추방하거나, 본인이 스스로 탈퇴한다.")
    @DeleteMapping("/{teamId}/members/{memberId}")
    public GlobalApiResponse<Void> removeTeamMember(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long teamId,
            @PathVariable Long memberId) {

        removeTeamMemberUseCase.remove(teamId, authUser.userId(), memberId);

        return GlobalApiResponse.ok(TeamResponseCode.TEAM_MEMBER_REMOVED);
    }

    @Operation(summary = "팀원 목록 및 역할 조회", description = "팀에 소속된 팀원과 각자의 역할(MEMBER/ADMIN)을 조회한다.")
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

    @Operation(summary = "협업 규칙 조회", description = "팀원이 팀의 협업 규칙(Team Collaboration Charter)을 조회한다. 아직 없으면 404를 반환한다.")
    @GetMapping("/{teamId}/collaboration-rule")
    public GlobalApiResponse<CollaborationRuleResponse> getCollaborationRule(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long teamId) {

        TeamCollaborationRuleEntity rule = getCollaborationRuleUseCase.getRule(teamId, authUser.userId());

        return GlobalApiResponse.ok(TeamResponseCode.COLLABORATION_RULE_FETCHED, CollaborationRuleResponse.from(rule));
    }

    @Operation(summary = "협업 규칙 수정", description = "팀 관리자가 협업 규칙 내용을 수정한다. 아직 없으면 DRAFT 상태로 새로 생성하고, 이미 채택(ADOPTED)된 규칙이어도 수정하면 다시 DRAFT로 돌아간다.")
    @PutMapping("/{teamId}/collaboration-rule")
    public GlobalApiResponse<CollaborationRuleResponse> upsertCollaborationRule(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long teamId,
            @RequestBody @Valid UpsertCollaborationRuleRequest request) {

        UpsertCollaborationRuleCommand command = new UpsertCollaborationRuleCommand(request.content());
        TeamCollaborationRuleEntity rule = upsertCollaborationRuleUseCase.upsert(teamId, authUser.userId(), command);

        return GlobalApiResponse.ok(TeamResponseCode.COLLABORATION_RULE_UPSERTED, CollaborationRuleResponse.from(rule));
    }

    @Operation(summary = "협업 규칙 채택", description = "팀 관리자가 DRAFT 상태의 협업 규칙을 ADOPTED로 확정한다.")
    @PostMapping("/{teamId}/collaboration-rule/adopt")
    public GlobalApiResponse<CollaborationRuleResponse> adoptCollaborationRule(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long teamId) {

        TeamCollaborationRuleEntity rule = adoptCollaborationRuleUseCase.adopt(teamId, authUser.userId());

        return GlobalApiResponse.ok(TeamResponseCode.COLLABORATION_RULE_ADOPTED, CollaborationRuleResponse.from(rule));
    }
}
