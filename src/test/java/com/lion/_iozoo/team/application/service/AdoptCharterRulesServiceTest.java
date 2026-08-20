package com.lion._iozoo.team.application.service;

import com.lion._iozoo.global.exception.ForbiddenException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.application.command.AdoptCharterRulesCommand;
import com.lion._iozoo.team.application.port.out.RequestAdoptCharterRulesPort;
import com.lion._iozoo.user.application.port.out.LoadUserPort;
import com.lion._iozoo.user.domain.User;
import com.lion._iozoo.user.domain.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdoptCharterRulesServiceTest {

    @Mock
    private TeamPermissionChecker teamPermissionChecker;
    @Mock
    private LoadUserPort loadUserPort;
    @Mock
    private RequestAdoptCharterRulesPort requestAdoptCharterRulesPort;

    private AdoptCharterRulesService sut() {
        return new AdoptCharterRulesService(teamPermissionChecker, loadUserPort, requestAdoptCharterRulesPort);
    }

    private User user(Long id, UUID publicId) {
        return User.builder()
                .id(id).publicId(publicId).email("a@b.com").password("hashed")
                .name("관리자").timezone("Asia/Seoul").language("ko")
                .build();
    }

    @Test
    void 관리자면_규칙들을_채택한다() {
        UUID publicId = UUID.randomUUID();
        when(loadUserPort.loadUserById(10L)).thenReturn(Optional.of(user(10L, publicId)));

        sut().adopt(1L, 10L, new AdoptCharterRulesCommand(List.of("uuid-1", "uuid-2")));

        verify(teamPermissionChecker).requireAdmin(1L, 10L);
        verify(requestAdoptCharterRulesPort).adopt(1L, List.of("uuid-1", "uuid-2"), publicId);
    }

    @Test
    void 관리자가_아니면_예외() {
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireAdmin(1L, 99L);

        assertThatThrownBy(() -> sut().adopt(1L, 99L, new AdoptCharterRulesCommand(List.of("uuid-1"))))
                .isInstanceOf(ForbiddenException.class);

        verify(requestAdoptCharterRulesPort, never()).adopt(org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void 사용자가_없으면_예외() {
        when(loadUserPort.loadUserById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().adopt(1L, 10L, new AdoptCharterRulesCommand(List.of("uuid-1"))))
                .isInstanceOf(UserNotFoundException.class);
    }
}
