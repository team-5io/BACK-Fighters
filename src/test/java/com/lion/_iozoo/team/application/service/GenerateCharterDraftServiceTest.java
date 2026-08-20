package com.lion._iozoo.team.application.service;

import com.lion._iozoo.global.exception.ForbiddenException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.application.port.out.RequestCharterDraftPort;
import com.lion._iozoo.team.application.result.CharterRule;
import com.lion._iozoo.team.domain.exception.CharterGatewayFailedException;
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
class GenerateCharterDraftServiceTest {

    @Mock
    private TeamPermissionChecker teamPermissionChecker;
    @Mock
    private RequestCharterDraftPort requestCharterDraftPort;

    private GenerateCharterDraftService sut() {
        return new GenerateCharterDraftService(teamPermissionChecker, requestCharterDraftPort);
    }

    @Test
    void 관리자면_AI가_생성한_규칙_목록을_그대로_반환한다() {
        List<CharterRule> rules = List.of(
                new CharterRule("uuid-1", "draft", "리뷰 SLA", "24시간 이내 리뷰"),
                new CharterRule("uuid-2", "draft", "소통 채널", "슬랙 #urgent"));
        when(requestCharterDraftPort.requestDraft(1L)).thenReturn(rules);

        List<CharterRule> result = sut().generate(1L, 10L);

        assertThat(result).isEqualTo(rules);
        verify(teamPermissionChecker).requireAdmin(1L, 10L);
    }

    @Test
    void 관리자가_아니면_예외() {
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireAdmin(1L, 99L);

        assertThatThrownBy(() -> sut().generate(1L, 99L))
                .isInstanceOf(ForbiddenException.class);

        verify(requestCharterDraftPort, never()).requestDraft(1L);
    }

    @Test
    void AI_Gateway_호출이_실패하면_예외가_전파된다() {
        when(requestCharterDraftPort.requestDraft(1L)).thenThrow(new CharterGatewayFailedException("generate teamId=1", null));

        assertThatThrownBy(() -> sut().generate(1L, 10L))
                .isInstanceOf(CharterGatewayFailedException.class);
    }
}
