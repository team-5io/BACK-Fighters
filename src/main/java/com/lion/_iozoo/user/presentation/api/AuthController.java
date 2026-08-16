package com.lion._iozoo.user.presentation.api;

import com.lion._iozoo.user.application.command.LoginCommand;
import com.lion._iozoo.user.application.command.SignupCommand;
import com.lion._iozoo.user.application.result.LoginResult;
import com.lion._iozoo.user.application.usecase.LoginUseCase;
import com.lion._iozoo.user.application.usecase.LogoutUseCase;
import com.lion._iozoo.user.application.usecase.SignupUseCase;
import com.lion._iozoo.user.domain.User;
import com.lion._iozoo.user.presentation.api.request.LoginRequest;
import com.lion._iozoo.user.presentation.api.request.SignupRequest;
import com.lion._iozoo.user.presentation.api.response.LoginResponse;
import com.lion._iozoo.user.presentation.api.response.SignupResponse;
import com.lion._iozoo.global.presentation.GlobalApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lion._iozoo.user.presentation.api.request.LogoutRequest;
import com.lion._iozoo.user.presentation.api.common.UserResponseCode;


@Tag(name = "Auth", description = "회원가입/로그인/로그아웃 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SignupUseCase signupUseCase;
    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;

    @Operation(summary = "회원가입", description = "이메일/비밀번호/이름으로 새 계정을 생성한다.")
    @PostMapping("/signup")
    public GlobalApiResponse<SignupResponse> signup(@RequestBody @Valid SignupRequest request) {
        // 1. HTTP Request 객체를 비즈니스 로직 전용 Command 객체로 변환합니다[cite: 2].
        SignupCommand command = new SignupCommand(
                request.email(),
                request.password(),
                request.name()
        );

        // 2. UseCase를 호출하여 비즈니스 로직을 실행합니다.
        User savedUser = signupUseCase.signup(command);

        // 3. 반환된 도메인 객체를 HTTP Response 객체로 변환하고, 공통 껍데기에 담아 반환합니다[cite: 2].
        return GlobalApiResponse.created(UserResponseCode.SIGNUP_SUCCESS, SignupResponse.from(savedUser));
    }

    @Operation(summary = "로그인", description = "이메일/비밀번호로 인증하고 액세스 토큰을 발급한다.")
    @PostMapping("/login")
    public GlobalApiResponse<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginCommand command = new LoginCommand(request.email(), request.password());

        // 1. UseCase에서 유저 인증(이메일, 비밀번호 확인) 및 액세스 토큰 발급까지 처리
        LoginResult result = loginUseCase.login(command);

        // 2. 유저 정보와 토큰을 함께 응답 DTO에 담아서 반환
        return GlobalApiResponse.ok(UserResponseCode.LOGIN_SUCCESS, LoginResponse.of(result.user(), result.accessToken()));
    }

    @Operation(summary = "로그아웃", description = "전달된 액세스 토큰을 블랙리스트에 등록해 무효화한다.")
    @PostMapping("/logout")
    public GlobalApiResponse<Void> logout(@RequestBody @Valid LogoutRequest request) {
        logoutUseCase.logout(request.accessToken());

        return GlobalApiResponse.ok(UserResponseCode.LOGOUT_SUCCESS);
    }
}
