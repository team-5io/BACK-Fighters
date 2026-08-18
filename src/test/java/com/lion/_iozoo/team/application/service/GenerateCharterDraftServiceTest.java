package com.lion._iozoo.team.application.service;

import com.lion._iozoo.team.application.command.UpsertCollaborationRuleCommand;
import com.lion._iozoo.team.application.port.out.RequestCharterDraftPort;
import com.lion._iozoo.team.application.result.CharterRuleDraft;
import com.lion._iozoo.team.application.usecase.UpsertCollaborationRuleUseCase;
import com.lion._iozoo.team.domain.CollaborationRuleStatus;
import com.lion._iozoo.team.domain.exception.CharterDraftFailedException;
import com.lion._iozoo.global.exception.ForbiddenException;
import com.lion._iozoo.team.infrastructure.persistence.TeamCollaborationRuleEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerateCharterDraftServiceTest {

    @Mock
    private RequestCharterDraftPort requestCharterDraftPort;
    @Mock
    private UpsertCollaborationRuleUseCase upsertCollaborationRuleUseCase;

    private GenerateCharterDraftService sut() {
        return new GenerateCharterDraftService(requestCharterDraftPort, upsertCollaborationRuleUseCase);
    }

    @Test
    void AI가_생성한_여러_규칙을_번호_매긴_텍스트로_합쳐서_DRAFT로_저장한다() {
        when(requestCharterDraftPort.requestDraft(1L)).thenReturn(List.of(
                new CharterRuleDraft("리뷰 SLA", "24시간 이내 리뷰"),
                new CharterRuleDraft("소통 채널", "슬랙 #urgent")));
        TeamCollaborationRuleEntity saved = TeamCollaborationRuleEntity.builder()
                .id(1L).teamId(1L)
                .content("1. 리뷰 SLA\n24시간 이내 리뷰\n\n2. 소통 채널\n슬랙 #urgent")
                .status(CollaborationRuleStatus.DRAFT)
                .build();
        when(upsertCollaborationRuleUseCase.upsert(eq(1L), eq(10L), any())).thenReturn(saved);

        TeamCollaborationRuleEntity result = sut().generate(1L, 10L);

        assertThat(result.getContent()).isEqualTo("1. 리뷰 SLA\n24시간 이내 리뷰\n\n2. 소통 채널\n슬랙 #urgent");
        assertThat(result.getStatus()).isEqualTo(CollaborationRuleStatus.DRAFT);
        verify(upsertCollaborationRuleUseCase).upsert(1L, 10L,
                new UpsertCollaborationRuleCommand(
                        "1. 리뷰 SLA\n24시간 이내 리뷰\n\n2. 소통 채널\n슬랙 #urgent", CollaborationRuleStatus.DRAFT));
    }

    @Test
    void AI_Gateway_호출이_실패하면_예외가_전파되고_upsert는_호출되지_않는다() {
        when(requestCharterDraftPort.requestDraft(1L)).thenThrow(new CharterDraftFailedException(1L, null));

        assertThatThrownBy(() -> sut().generate(1L, 10L))
                .isInstanceOf(CharterDraftFailedException.class);

        verify(upsertCollaborationRuleUseCase, never()).upsert(any(), any(), any());
    }

    @Test
    void 관리자가_아니면_upsert에서_예외가_전파된다() {
        when(requestCharterDraftPort.requestDraft(1L)).thenReturn(List.of(new CharterRuleDraft("규칙", "설명")));
        when(upsertCollaborationRuleUseCase.upsert(eq(1L), eq(99L), any())).thenThrow(new ForbiddenException());

        assertThatThrownBy(() -> sut().generate(1L, 99L))
                .isInstanceOf(ForbiddenException.class);
    }
}
