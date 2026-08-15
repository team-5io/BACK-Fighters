package com.lion._iozoo.user.application.service;

import com.lion._iozoo.user.application.command.SignupCommand;
import com.lion._iozoo.user.application.port.out.LoadUserPort;
import com.lion._iozoo.user.application.port.out.SaveUserPort;
import com.lion._iozoo.user.application.usecase.SignupUseCase;
import com.lion._iozoo.user.domain.User;
import com.lion._iozoo.user.domain.exception.EmailDuplicateException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SignupService implements SignupUseCase {

    private final SaveUserPort saveUserPort;
    private final LoadUserPort loadUserPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User signup(SignupCommand command) {
        // 1. 이메일 중복 검증
        if (loadUserPort.loadUserByEmail(command.email()).isPresent()) {
            throw new EmailDuplicateException();
        }

        // 2. 도메인 객체 생성 (비밀번호는 인코딩해서 저장, publicId는 외부 노출용으로 가입 시 발급)
        User newUser = User.builder()
                .publicId(UUID.randomUUID())
                .email(command.email())
                .password(passwordEncoder.encode(command.password()))
                .name(command.name())
                .build();

        // 3. Port를 통해 외부 인프라(DB)로 저장 위임
        return saveUserPort.saveUser(newUser);
    }
}
