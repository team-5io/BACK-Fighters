package com.lion._iozoo.team.application.service;

import com.lion._iozoo.global.exception.ForbiddenException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.domain.CollaborationRuleStatus;
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
class GetCollaborationRuleServiceTest {

    @Mock
    private TeamCollaborationRuleRepository teamCollaborationRuleRepository;
    @Mock
    private TeamPermissionChecker teamPermissionChecker;

    private GetCollaborationRuleService sut() {
        return new GetCollaborationRuleService(teamCollaborationRuleRepository, teamPermissionChecker);
    }

    private TeamCollaborationRuleEntity rule() {
        return TeamCollaborationRuleEntity.builder()
                .id(1L).teamId(1L).content("초안 공유는 24시간 내").status(CollaborationRuleStatus.DRAFT)
                .build();
    }

    @Test
    void 팀원이_협업_규칙을_조회한다() {
        when(teamCollaborationRuleRepository.findByTeamId(1L)).thenReturn(Optional.of(rule()));

        TeamCollaborationRuleEntity result = sut().getRule(1L, 10L);

        assertThat(result.getContent()).isEqualTo("초안 공유는 24시간 내");
    }

    @Test
    void 규칙이_없으면_예외() {
        when(teamCollaborationRuleRepository.findByTeamId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().getRule(1L, 10L))
                .isInstanceOf(CollaborationRuleNotFoundException.class);
    }

    @Test
    void 팀원이_아니면_예외() {
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireMember(1L, 99L);

        assertThatThrownBy(() -> sut().getRule(1L, 99L))
                .isInstanceOf(ForbiddenException.class);
    }
}
