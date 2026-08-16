package com.lion._iozoo.user.application.service;

import com.lion._iozoo.user.application.command.SignupCommand;
import com.lion._iozoo.user.application.port.out.LoadUserPort;
import com.lion._iozoo.user.application.port.out.SaveUserPort;
import com.lion._iozoo.user.application.usecase.SignupUseCase;
import com.lion._iozoo.user.domain.User;
import com.lion._iozoo.user.domain.exception.EmailDuplicateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignupService implements SignupUseCase {

    private final SaveUserPort saveUserPort;
    private final LoadUserPort loadUserPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User signup(SignupCommand command) {
        log.info("event=user_signup_시작 email={}", command.email());

        try {
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
            User saved = saveUserPort.saveUser(newUser);

            log.info("event=user_signup_완료 email={}, userId={}", command.email(), saved.getId());
            return saved;
        } catch (RuntimeException e) {
            log.warn("event=user_signup_실패 email={}, reason={}", command.email(), e.getMessage(), e);
            throw e;
        }
    }
}
