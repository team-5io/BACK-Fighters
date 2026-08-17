package com.lion._iozoo.team.application.service;

import com.lion._iozoo.global.exception.ForbiddenException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.domain.CollaborationRuleStatus;
import com.lion._iozoo.team.domain.exception.CollaborationRuleAlreadyAdoptedException;
import com.lion._iozoo.team.domain.exception.CollaborationRuleNotFoundException;
import com.lion._iozoo.team.infrastructure.persistence.TeamCollaborationRuleEntity;
import com.lion._iozoo.team.infrastructure.persistence.TeamCollaborationRuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdoptCollaborationRuleServiceTest {

    @Mock
    private TeamCollaborationRuleRepository teamCollaborationRuleRepository;
    @Mock
    private TeamPermissionChecker teamPermissionChecker;

    private AdoptCollaborationRuleService sut() {
        return new AdoptCollaborationRuleService(teamCollaborationRuleRepository, teamPermissionChecker);
    }

    private TeamCollaborationRuleEntity rule(CollaborationRuleStatus status) {
        return TeamCollaborationRuleEntity.builder()
                .id(1L).teamId(1L).content("규칙 내용").status(status)
                .build();
    }

    @Test
    void 관리자가_DRAFT_규칙을_ADOPTED로_확정한다() {
        when(teamCollaborationRuleRepository.findByTeamId(1L)).thenReturn(Optional.of(rule(CollaborationRuleStatus.DRAFT)));

        TeamCollaborationRuleEntity result = sut().adopt(1L, 10L);

        assertThat(result.getStatus()).isEqualTo(CollaborationRuleStatus.ADOPTED);
    }

    @Test
    void 규칙이_없으면_예외() {
        when(teamCollaborationRuleRepository.findByTeamId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().adopt(1L, 10L))
                .isInstanceOf(CollaborationRuleNotFoundException.class);
    }

    @Test
    void 이미_ADOPTED면_예외() {
        when(teamCollaborationRuleRepository.findByTeamId(1L)).thenReturn(Optional.of(rule(CollaborationRuleStatus.ADOPTED)));

        assertThatThrownBy(() -> sut().adopt(1L, 10L))
                .isInstanceOf(CollaborationRuleAlreadyAdoptedException.class);
    }

    @Test
    void 관리자가_아니면_예외() {
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireAdmin(1L, 99L);

        assertThatThrownBy(() -> sut().adopt(1L, 99L))
                .isInstanceOf(ForbiddenException.class);
    }
}
