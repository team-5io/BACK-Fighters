package com.lion._iozoo.team.application.service;

import com.lion._iozoo.global.exception.ForbiddenException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.application.command.CreateCharterRuleCommand;
import com.lion._iozoo.team.domain.CharterRuleStatus;
import com.lion._iozoo.team.infrastructure.persistence.CharterRuleEntity;
import com.lion._iozoo.team.infrastructure.persistence.CharterRuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCharterRuleServiceTest {

    @Mock
    private TeamPermissionChecker teamPermissionChecker;
    @Mock
    private CharterRuleRepository charterRuleRepository;

    private CreateCharterRuleService sut() {
        return new CreateCharterRuleService(teamPermissionChecker, charterRuleRepository);
    }

    @Test
    void 관리자면_DRAFT_상태로_규칙을_생성한다() {
        when(charterRuleRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CharterRuleEntity result = sut().create(1L, 10L, new CreateCharterRuleCommand("리뷰 SLA", "24시간 이내 리뷰"));

        assertThat(result.getTeamId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("리뷰 SLA");
        assertThat(result.getContent()).isEqualTo("24시간 이내 리뷰");
        assertThat(result.getStatus()).isEqualTo(CharterRuleStatus.DRAFT);
        verify(teamPermissionChecker).requireAdmin(1L, 10L);
    }

    @Test
    void 관리자가_아니면_예외() {
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireAdmin(1L, 99L);

        assertThatThrownBy(() -> sut().create(1L, 99L, new CreateCharterRuleCommand("제목", "내용")))
                .isInstanceOf(ForbiddenException.class);

        verify(charterRuleRepository, never()).save(any());
    }
}
