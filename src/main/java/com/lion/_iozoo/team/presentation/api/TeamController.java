package com.lion._iozoo.team.presentation.api;

import com.lion._iozoo.global.presentation.GlobalApiResponse;
import com.lion._iozoo.global.security.AuthUser;
import com.lion._iozoo.team.application.command.AdoptCharterRulesCommand;
import com.lion._iozoo.team.application.command.CreateCharterRuleCommand;
import com.lion._iozoo.team.application.command.CreateTeamCommand;
import com.lion._iozoo.team.application.command.InviteTeamMemberCommand;
import com.lion._iozoo.team.application.command.UpdateCharterRuleCommand;
import com.lion._iozoo.team.application.usecase.AdoptCharterRulesUseCase;
import com.lion._iozoo.team.application.usecase.CreateCharterRuleUseCase;
import com.lion._iozoo.team.application.usecase.CreateTeamUseCase;
import com.lion._iozoo.team.application.usecase.DeleteCharterRuleUseCase;
import com.lion._iozoo.team.application.usecase.GenerateCharterDraftUseCase;
import com.lion._iozoo.team.application.usecase.InviteTeamMemberUseCase;
import com.lion._iozoo.team.application.usecase.ListCharterRulesUseCase;
import com.lion._iozoo.team.application.usecase.ListMyTeamsUseCase;
import com.lion._iozoo.team.application.usecase.ListTeamMembersUseCase;
import com.lion._iozoo.team.application.usecase.RemoveTeamMemberUseCase;
import com.lion._iozoo.team.application.usecase.UpdateCharterRuleUseCase;
import com.lion._iozoo.team.application.result.TeamMemberResult;
import com.lion._iozoo.team.domain.CharterRuleStatus;
import com.lion._iozoo.team.domain.TeamRole;
import com.lion._iozoo.team.infrastructure.persistence.CharterRuleEntity;
import com.lion._iozoo.team.infrastructure.persistence.TeamEntity;
import com.lion._iozoo.team.presentation.api.common.TeamResponseCode;
import com.lion._iozoo.team.presentation.api.request.AdoptCharterRulesRequest;
import com.lion._iozoo.team.presentation.api.request.CreateCharterRuleRequest;
import com.lion._iozoo.team.presentation.api.request.CreateTeamRequest;
import com.lion._iozoo.team.presentation.api.request.InviteTeamMemberRequest;
import com.lion._iozoo.team.presentation.api.request.UpdateCharterRuleRequest;
import com.lion._iozoo.team.presentation.api.response.CharterRuleResponse;
import com.lion._iozoo.team.presentation.api.response.TeamMemberResponse;
import com.lion._iozoo.team.presentation.api.response.TeamResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    private final ListMyTeamsUseCase listMyTeamsUseCase;
    private final GenerateCharterDraftUseCase generateCharterDraftUseCase;
    private final CreateCharterRuleUseCase createCharterRuleUseCase;
    private final ListCharterRulesUseCase listCharterRulesUseCase;
    private final UpdateCharterRuleUseCase updateCharterRuleUseCase;
    private final DeleteCharterRuleUseCase deleteCharterRuleUseCase;
    private final AdoptCharterRulesUseCase adoptCharterRulesUseCase;

    @Operation(summary = "내가 소속된 팀 목록 조회", description = "로그인한 유저가 속한 팀 목록을 조회한다.")
    @GetMapping("/me")
    public GlobalApiResponse<List<TeamResponse>> listMyTeams(@AuthenticationPrincipal AuthUser authUser) {
        List<TeamResponse> teams = listMyTeamsUseCase.listMyTeams(authUser.userId())
                .stream()
                .map(result -> TeamResponse.from(result.team(), result.role()))
                .toList();

        return GlobalApiResponse.ok(TeamResponseCode.MY_TEAMS_FETCHED, teams);
    }

    @Operation(summary = "팀 생성", description = "새 협업 팀 공간을 생성한다. 생성자는 자동으로 ADMIN이 된다.")
    @PostMapping
    public GlobalApiResponse<TeamResponse> createTeam(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody @Valid CreateTeamRequest request) {

        CreateTeamCommand command = new CreateTeamCommand(request.name());
        TeamEntity team = createTeamUseCase.createTeam(authUser.userId(), command);

        return GlobalApiResponse.created(TeamResponseCode.TEAM_CREATED, TeamResponse.from(team, TeamRole.ADMIN));
    }

    @Operation(summary = "팀원 초대", description = "ADMIN이 이미 가입된 유저를 이메일로 팀에 초대해 MEMBER로 등록한다.")
    @PostMapping("/{teamId}/invitations")
    public GlobalApiResponse<TeamMemberResponse> inviteTeamMember(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long teamId,
            @RequestBody @Valid InviteTeamMemberRequest request) {

        InviteTeamMemberCommand command = new InviteTeamMemberCommand(request.email());
        TeamMemberResult teamMember = inviteTeamMemberUseCase.invite(teamId, authUser.userId(), command);

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

    @Operation(summary = "협업 규칙 목록 조회", description = "팀원이 팀의 협업 규칙(Team Collaboration Charter) 목록을 조회한다. status로 DRAFT/ADOPTED 필터 가능(미지정 시 전체).")
    @GetMapping("/{teamId}/charter/rules")
    public GlobalApiResponse<List<CharterRuleResponse>> listCharterRules(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long teamId,
            @RequestParam(required = false) CharterRuleStatus status) {

        List<CharterRuleResponse> rules = listCharterRulesUseCase.list(teamId, authUser.userId(), status)
                .stream()
                .map(CharterRuleResponse::from)
                .toList();

        return GlobalApiResponse.ok(TeamResponseCode.CHARTER_RULES_FETCHED, rules);
    }

    @Operation(summary = "협업 규칙 초안 생성 요청", description = "팀 관리자가 AI(Team Collaboration Charter)에게 협업 규칙 초안 생성을 요청한다. AI가 제안한 규칙들을 DRAFT 상태로 저장하고 반환한다. (AI 기능은 이 초안 생성뿐이고, 이후 조회/수정/삭제/채택은 전부 BE 자체 CRUD)")
    @PostMapping("/{teamId}/charter/draft")
    public GlobalApiResponse<List<CharterRuleResponse>> generateCharterDraft(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long teamId) {

        List<CharterRuleResponse> rules = generateCharterDraftUseCase.generate(teamId, authUser.userId())
                .stream()
                .map(CharterRuleResponse::from)
                .toList();

        return GlobalApiResponse.ok(TeamResponseCode.CHARTER_DRAFT_GENERATED, rules);
    }

    @Operation(summary = "협업 규칙 추가", description = "팀 관리자가 협업 규칙을 직접 작성해 새로 추가한다 (DRAFT 상태로 생성).")
    @PostMapping("/{teamId}/charter/rules")
    public GlobalApiResponse<CharterRuleResponse> createCharterRule(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long teamId,
            @RequestBody @Valid CreateCharterRuleRequest request) {

        CreateCharterRuleCommand command = new CreateCharterRuleCommand(request.title(), request.content());
        CharterRuleEntity rule = createCharterRuleUseCase.create(teamId, authUser.userId(), command);

        return GlobalApiResponse.created(TeamResponseCode.CHARTER_RULE_CREATED, CharterRuleResponse.from(rule));
    }

    @Operation(summary = "협업 규칙 수정", description = "팀 관리자가 협업 규칙 하나의 제목/내용을 수정한다.")
    @PatchMapping("/{teamId}/charter/rules/{ruleId}")
    public GlobalApiResponse<CharterRuleResponse> updateCharterRule(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long teamId,
            @PathVariable Long ruleId,
            @RequestBody @Valid UpdateCharterRuleRequest request) {

        UpdateCharterRuleCommand command = new UpdateCharterRuleCommand(request.title(), request.content());
        CharterRuleEntity rule = updateCharterRuleUseCase.update(teamId, authUser.userId(), ruleId, command);

        return GlobalApiResponse.ok(TeamResponseCode.CHARTER_RULE_UPDATED, CharterRuleResponse.from(rule));
    }

    @Operation(summary = "협업 규칙 삭제", description = "팀 관리자가 협업 규칙을 삭제한다.")
    @DeleteMapping("/{teamId}/charter/rules/{ruleId}")
    public GlobalApiResponse<Void> deleteCharterRule(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long teamId,
            @PathVariable Long ruleId) {

        deleteCharterRuleUseCase.delete(teamId, authUser.userId(), ruleId);

        return GlobalApiResponse.ok(TeamResponseCode.CHARTER_RULE_DELETED);
    }

    @Operation(summary = "협업 규칙 채택", description = "팀 관리자가 지정한 규칙들을 공식 규칙(ADOPTED)으로 일괄 확정한다.")
    @PostMapping("/{teamId}/charter/adopt")
    public GlobalApiResponse<List<CharterRuleResponse>> adoptCharterRules(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long teamId,
            @RequestBody @Valid AdoptCharterRulesRequest request) {

        List<CharterRuleResponse> rules = adoptCharterRulesUseCase
                .adopt(teamId, authUser.userId(), new AdoptCharterRulesCommand(request.ruleIds()))
                .stream()
                .map(CharterRuleResponse::from)
                .toList();

        return GlobalApiResponse.ok(TeamResponseCode.CHARTER_RULES_ADOPTED, rules);
    }
}
