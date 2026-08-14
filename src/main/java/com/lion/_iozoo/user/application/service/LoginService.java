package com.lion._iozoo.user.application.service;

import com.lion._iozoo.global.security.JwtTokenProvider;
import com.lion._iozoo.user.application.command.LoginCommand;
import com.lion._iozoo.user.application.port.out.LoadUserPort;
import com.lion._iozoo.user.application.result.LoginResult;
import com.lion._iozoo.user.application.usecase.LoginUseCase;
import com.lion._iozoo.user.domain.User;
import com.lion._iozoo.user.domain.exception.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginService implements LoginUseCase {

    private final LoadUserPort loadUserPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional(readOnly = true)
    public LoginResult login(LoginCommand command) {
        // 1. 포트를 통해 DB에서 이메일로 회원 조회
        // 이메일 존재 여부와 비밀번호 불일치를 구분하지 않는다(계정 열거 공격 방지).
        User user = loadUserPort.loadUserByEmail(command.email())
                .orElseThrow(InvalidCredentialsException::new);

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(command.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        // 3. 인증된 유저의 ID로 액세스 토큰 발급
        String accessToken = jwtTokenProvider.createAccessToken(user.getId());

        return new LoginResult(user, accessToken);
    }
}
