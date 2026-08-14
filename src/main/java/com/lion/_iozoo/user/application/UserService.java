package com.lion._iozoo.user.application;

import com.lion._iozoo.user.application.command.LoginCommand;
import com.lion._iozoo.user.application.command.SignupCommand;
import com.lion._iozoo.user.application.command.UpdateProfileCommand;
import com.lion._iozoo.user.application.port.out.LoadUserPort;
import com.lion._iozoo.user.application.port.out.SaveUserPort;
import com.lion._iozoo.user.domain.User;
import com.lion._iozoo.user.domain.exception.EmailDuplicateException;
import com.lion._iozoo.user.domain.exception.InvalidCredentialsException;
import com.lion._iozoo.user.domain.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final SaveUserPort saveUserPort;
    private final LoadUserPort loadUserPort;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User signup(SignupCommand command) {
        // 1. 이메일 중복 검증
        if (loadUserPort.loadUserByEmail(command.email()).isPresent()) {
            throw new EmailDuplicateException();
        }

        // 2. 도메인 객체 생성 (비밀번호는 인코딩해서 저장)
        User newUser = User.builder()
                .email(command.email())
                .password(passwordEncoder.encode(command.password()))
                .name(command.name())
                .build();

        // 3. Port를 통해 외부 인프라(DB)로 저장 위임
        return saveUserPort.saveUser(newUser);
    }

    @Transactional(readOnly = true)
    public User login(LoginCommand command) {
        // 1. 포트를 통해 DB에서 이메일로 회원 조회
        // 이메일 존재 여부와 비밀번호 불일치를 구분하지 않는다(계정 열거 공격 방지).
        User user = loadUserPort.loadUserByEmail(command.email())
                .orElseThrow(InvalidCredentialsException::new);

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(command.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return user;
    }

    @Transactional
    public User updateProfile(Long userId, UpdateProfileCommand command) {
        // 1. ID로 기존 유저 조회
        User user = loadUserPort.loadUserById(userId)
                .orElseThrow(UserNotFoundException::new);

        // 2. 도메인 객체의 이름, 시간대, 선호 언어 변경
        user.updateProfile(command.name(), command.timezone(), command.language());

        // 3. 변경된 객체를 다시 저장 (JPA가 알아서 UPDATE 쿼리를 날려줍니다)
        return saveUserPort.saveUser(user);
    }
}