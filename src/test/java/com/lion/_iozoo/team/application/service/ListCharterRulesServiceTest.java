package com.lion._iozoo.team.application.service;

import com.lion._iozoo.global.exception.ForbiddenException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.domain.CharterRuleStatus;
import com.lion._iozoo.team.infrastructure.persistence.CharterRuleEntity;
import com.lion._iozoo.team.infrastructure.persistence.CharterRuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListCharterRulesServiceTest {

    @Mock
    private TeamPermissionChecker teamPermissionChecker;
    @Mock
    private CharterRuleRepository charterRuleRepository;

    private ListCharterRulesService sut() {
        return new ListCharterRulesService(teamPermissionChecker, charterRuleRepository);
    }

    private CharterRuleEntity rule() {
        return CharterRuleEntity.builder()
                .id(1L).teamId(1L).title("리뷰 SLA").content("24시간 이내 리뷰")
                .status(CharterRuleStatus.ADOPTED).build();
    }

    @Test
    void 팀원이면_상태로_필터링된_규칙_목록을_조회한다() {
        when(charterRuleRepository.findByTeamIdAndStatus(1L, CharterRuleStatus.ADOPTED)).thenReturn(List.of(rule()));

        List<CharterRuleEntity> result = sut().list(1L, 10L, CharterRuleStatus.ADOPTED);

        assertThat(result).hasSize(1);
        verify(teamPermissionChecker).requireMember(1L, 10L);
        verify(charterRuleRepository, never()).findByTeamId(1L);
    }

    @Test
    void status가_없으면_전체를_조회한다() {
        when(charterRuleRepository.findByTeamId(1L)).thenReturn(List.of());

        sut().list(1L, 10L, null);

        verify(charterRuleRepository).findByTeamId(1L);
    }

    @Test
    void 팀원이_아니면_예외() {
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireMember(1L, 99L);

        assertThatThrownBy(() -> sut().list(1L, 99L, null))
                .isInstanceOf(ForbiddenException.class);
    }
}
