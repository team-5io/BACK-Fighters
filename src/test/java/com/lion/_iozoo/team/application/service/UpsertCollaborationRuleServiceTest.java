package com.lion._iozoo.team.application.service;

import com.lion._iozoo.global.exception.ForbiddenException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.application.command.UpsertCollaborationRuleCommand;
import com.lion._iozoo.team.domain.CollaborationRuleStatus;
import com.lion._iozoo.team.infrastructure.persistence.TeamCollaborationRuleEntity;
import com.lion._iozoo.team.infrastructure.persistence.TeamCollaborationRuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpsertCollaborationRuleServiceTest {

    @Mock
    private TeamCollaborationRuleRepository teamCollaborationRuleRepository;
    @Mock
    private TeamPermissionChecker teamPermissionChecker;

    private UpsertCollaborationRuleService sut() {
        return new UpsertCollaborationRuleService(teamCollaborationRuleRepository, teamPermissionChecker);
    }

    @Test
    void 규칙이_없으면_지정한_내용_상태로_새로_생성한다() {
        when(teamCollaborationRuleRepository.findByTeamId(1L)).thenReturn(Optional.empty());
        when(teamCollaborationRuleRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TeamCollaborationRuleEntity result = sut().upsert(1L, 10L,
                new UpsertCollaborationRuleCommand("새 규칙", CollaborationRuleStatus.DRAFT));

        assertThat(result.getContent()).isEqualTo("새 규칙");
        assertThat(result.getStatus()).isEqualTo(CollaborationRuleStatus.DRAFT);
        verify(teamPermissionChecker).requireAdmin(1L, 10L);
    }

    @Test
    void 규칙이_있으면_내용과_상태를_그대로_덮어쓴다() {
        TeamCollaborationRuleEntity existing = TeamCollaborationRuleEntity.builder()
                .id(1L).teamId(1L).content("기존 규칙").status(CollaborationRuleStatus.DRAFT)
                .build();
        when(teamCollaborationRuleRepository.findByTeamId(1L)).thenReturn(Optional.of(existing));

        TeamCollaborationRuleEntity result = sut().upsert(1L, 10L,
                new UpsertCollaborationRuleCommand("확정 규칙", CollaborationRuleStatus.ADOPTED));

        assertThat(result.getContent()).isEqualTo("확정 규칙");
        assertThat(result.getStatus()).isEqualTo(CollaborationRuleStatus.ADOPTED);
        verify(teamCollaborationRuleRepository, never()).save(any());
    }

    @Test
    void 관리자가_아니면_예외() {
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireAdmin(1L, 99L);

        assertThatThrownBy(() -> sut().upsert(1L, 99L,
                new UpsertCollaborationRuleCommand("내용", CollaborationRuleStatus.DRAFT)))
                .isInstanceOf(ForbiddenException.class);

        verify(teamCollaborationRuleRepository, never()).findByTeamId(any());
    }
}
