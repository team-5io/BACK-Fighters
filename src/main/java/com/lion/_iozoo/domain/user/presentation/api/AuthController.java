package com.lion._iozoo.domain.user.presentation.api;

import com.lion._iozoo.domain.user.application.UserService;
import com.lion._iozoo.domain.user.application.command.LoginCommand;
import com.lion._iozoo.domain.user.application.command.SignupCommand;
import com.lion._iozoo.domain.user.domain.User;
import com.lion._iozoo.domain.user.presentation.api.request.LoginRequest;
import com.lion._iozoo.domain.user.presentation.api.request.SignupRequest;
import com.lion._iozoo.domain.user.presentation.api.response.LoginResponse;
import com.lion._iozoo.domain.user.presentation.api.response.SignupResponse;
import com.lion._iozoo.global.presentation.GlobalApiResponse;
import com.lion._iozoo.global.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lion._iozoo.domain.user.presentation.api.request.LogoutRequest;
import com.lion._iozoo.global.security.JwtBlacklist;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtBlacklist jwtBlacklist;

    @PostMapping("/signup")
    public GlobalApiResponse<SignupResponse> signup(@RequestBody @Valid SignupRequest request) {
        // 1. HTTP Request 객체를 비즈니스 로직 전용 Command 객체로 변환합니다[cite: 2].
        SignupCommand command = new SignupCommand(
                request.email(),
                request.password(),
                request.name()
        );

        // 2. Application Service를 호출하여 비즈니스 로직을 실행합니다.
        User savedUser = userService.signup(command);

        // 3. 반환된 도메인 객체를 HTTP Response 객체로 변환하고, 공통 껍데기에 담아 반환합니다[cite: 2].
        return GlobalApiResponse.created(SignupResponse.from(savedUser));
    }

    @PostMapping("/login")
    public GlobalApiResponse<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginCommand command = new LoginCommand(request.email(), request.password());

        // 1. 서비스 로직에서 유저 인증 (이메일, 비밀번호 확인)
        User loginUser = userService.login(command);

        // 2. 인증된 유저의 ID를 이용해 액세스 토큰 발급!
        String accessToken = jwtTokenProvider.createAccessToken(loginUser.getId());

        // 3. 유저 정보와 토큰을 함께 응답 DTO에 담아서 반환
        return GlobalApiResponse.ok(LoginResponse.of(loginUser, accessToken));
    }

    @PostMapping("/logout")
    public GlobalApiResponse<String> logout(@RequestBody @Valid LogoutRequest request) {
        // 프론트엔드에서 보낸 토큰을 블랙리스트에 등록하여 무효화 처리
        jwtBlacklist.add(request.accessToken());

        return GlobalApiResponse.ok("로그아웃 되었습니다.");
    }
}