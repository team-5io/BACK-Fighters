package com.lion._iozoo.user.presentation.api;

import com.lion._iozoo.global.security.AuthUser;
import com.lion._iozoo.global.security.JwtBlacklist;
import com.lion._iozoo.global.security.JwtTokenProvider;
import com.lion._iozoo.user.application.usecase.LoginUseCase;
import com.lion._iozoo.user.application.usecase.LogoutUseCase;
import com.lion._iozoo.user.application.usecase.SignupUseCase;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SignupUseCase signupUseCase;

    @MockBean
    private LoginUseCase loginUseCase;

    @MockBean
    private LogoutUseCase logoutUseCase;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtBlacklist jwtBlacklist;

    private UsernamePasswordAuthenticationToken authToken() {
        return new UsernamePasswordAuthenticationToken(new AuthUser(1L), null, List.of());
    }

    @Test
    @DisplayName("POST /auth/logout - Authorization 헤더의 토큰으로 로그아웃 성공")
    void logout_success() throws Exception {
        willDoNothing().given(logoutUseCase).logout("valid-access-token");

        mockMvc.perform(post("/auth/logout")
                        .with(authentication(authToken()))
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-access-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("USER_200_2"));

        then(logoutUseCase).should().logout(eq("valid-access-token"));
    }

    @Test
    @DisplayName("POST /auth/logout - Authorization 헤더가 없으면 401")
    void logout_missingHeader_returnsUnauthorized() throws Exception {
        mockMvc.perform(post("/auth/logout")
                        .with(authentication(authToken()))
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("COMMON_401_1"));

        then(logoutUseCase).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("POST /auth/logout - Bearer 접두사가 없으면 401")
    void logout_malformedHeader_returnsUnauthorized() throws Exception {
        mockMvc.perform(post("/auth/logout")
                        .with(authentication(authToken()))
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "valid-access-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("COMMON_401_1"));

        then(logoutUseCase).shouldHaveNoInteractions();
    }
}
