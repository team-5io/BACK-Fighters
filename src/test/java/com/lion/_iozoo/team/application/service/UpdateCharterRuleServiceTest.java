package com.lion._iozoo.team.application.service;

import com.lion._iozoo.global.exception.ForbiddenException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.application.command.UpdateCharterRuleCommand;
import com.lion._iozoo.team.application.port.out.RequestUpdateCharterRulePort;
import com.lion._iozoo.team.application.result.CharterRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateCharterRuleServiceTest {

    @Mock
    private TeamPermissionChecker teamPermissionChecker;
    @Mock
    private RequestUpdateCharterRulePort requestUpdateCharterRulePort;

    private UpdateCharterRuleService sut() {
        return new UpdateCharterRuleService(teamPermissionChecker, requestUpdateCharterRulePort);
    }

    @Test
    void 관리자면_규칙을_수정한다() {
        CharterRule updated = new CharterRule("uuid-1", "draft", "새 제목", "새 내용");
        when(requestUpdateCharterRulePort.updateRule("uuid-1", "새 제목", "새 내용")).thenReturn(updated);

        CharterRule result = sut().update(1L, 10L, "uuid-1", new UpdateCharterRuleCommand("새 제목", "새 내용"));

        assertThat(result).isEqualTo(updated);
        verify(teamPermissionChecker).requireAdmin(1L, 10L);
    }

    @Test
    void 관리자가_아니면_예외() {
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireAdmin(1L, 99L);

        assertThatThrownBy(() -> sut().update(1L, 99L, "uuid-1", new UpdateCharterRuleCommand("제목", "내용")))
                .isInstanceOf(ForbiddenException.class);

        verify(requestUpdateCharterRulePort, never()).updateRule("uuid-1", "제목", "내용");
    }
}
