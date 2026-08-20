package com.lion._iozoo.team.application.service;

import com.lion._iozoo.global.exception.ForbiddenException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.application.port.out.RequestCharterDraftPort;
import com.lion._iozoo.team.application.result.CharterRuleDraft;
import com.lion._iozoo.team.domain.CharterRuleStatus;
import com.lion._iozoo.team.domain.exception.CharterDraftFailedException;
import com.lion._iozoo.team.infrastructure.persistence.CharterRuleEntity;
import com.lion._iozoo.team.infrastructure.persistence.CharterRuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
    @Mock
    private CharterRuleRepository charterRuleRepository;

    private GenerateCharterDraftService sut() {
        return new GenerateCharterDraftService(teamPermissionChecker, requestCharterDraftPort, charterRuleRepository);
    }

    @Test
    void 관리자면_AI가_생성한_규칙들을_DRAFT로_저장한다() {
        when(requestCharterDraftPort.requestDraft(1L)).thenReturn(List.of(
                new CharterRuleDraft("리뷰 SLA", "24시간 이내 리뷰"),
                new CharterRuleDraft("소통 채널", "슬랙 #urgent")));
        when(charterRuleRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        List<CharterRuleEntity> result = sut().generate(1L, 10L);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(CharterRuleEntity::getTitle).containsExactly("리뷰 SLA", "소통 채널");
        assertThat(result).allMatch(rule -> rule.getStatus() == CharterRuleStatus.DRAFT);
        assertThat(result).allMatch(rule -> rule.getTeamId().equals(1L));
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
        when(requestCharterDraftPort.requestDraft(1L)).thenThrow(new CharterDraftFailedException(1L, null));

        assertThatThrownBy(() -> sut().generate(1L, 10L))
                .isInstanceOf(CharterDraftFailedException.class);
    }
}
