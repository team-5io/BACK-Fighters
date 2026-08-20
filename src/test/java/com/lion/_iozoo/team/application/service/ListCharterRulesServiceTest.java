package com.lion._iozoo.team.application.service;

import com.lion._iozoo.global.exception.ForbiddenException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.application.port.out.RequestCharterRulesPort;
import com.lion._iozoo.team.application.result.CharterRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListCharterRulesServiceTest {

    @Mock
    private TeamPermissionChecker teamPermissionChecker;
    @Mock
    private RequestCharterRulesPort requestCharterRulesPort;

    private ListCharterRulesService sut() {
        return new ListCharterRulesService(teamPermissionChecker, requestCharterRulesPort);
    }

    @Test
    void 팀원이면_규칙_목록을_조회한다() {
        List<CharterRule> rules = List.of(new CharterRule("uuid-1", "adopted", "리뷰 SLA", "24시간 이내 리뷰"));
        when(requestCharterRulesPort.listRules(1L, "adopted")).thenReturn(rules);

        List<CharterRule> result = sut().list(1L, 10L, "adopted");

        assertThat(result).isEqualTo(rules);
        verify(teamPermissionChecker).requireMember(1L, 10L);
    }

    @Test
    void status가_없으면_전체를_조회한다() {
        when(requestCharterRulesPort.listRules(1L, null)).thenReturn(List.of());

        sut().list(1L, 10L, null);

        verify(requestCharterRulesPort).listRules(1L, null);
    }

    @Test
    void 팀원이_아니면_예외() {
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireMember(1L, 99L);

        assertThatThrownBy(() -> sut().list(1L, 99L, null))
                .isInstanceOf(ForbiddenException.class);
    }
}
