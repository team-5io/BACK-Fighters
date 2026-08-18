package com.lion._iozoo.user.application.service;

import com.lion._iozoo.user.application.port.out.LoadUserPort;
import com.lion._iozoo.user.domain.User;
import com.lion._iozoo.user.domain.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetMyProfileServiceTest {

    @Mock
    private LoadUserPort loadUserPort;

    private GetMyProfileService sut() {
        return new GetMyProfileService(loadUserPort);
    }

    @Test
    void 내_정보를_조회한다() {
        User user = User.builder().id(10L).name("김재원").build();
        when(loadUserPort.loadUserById(10L)).thenReturn(Optional.of(user));

        User result = sut().getMyProfile(10L);

        assertThat(result.getName()).isEqualTo("김재원");
    }

    @Test
    void 유저가_없으면_예외() {
        when(loadUserPort.loadUserById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().getMyProfile(99L))
                .isInstanceOf(UserNotFoundException.class);
    }
}
