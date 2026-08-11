package com.lion._iozoo.domain.user.application;

import com.lion._iozoo.domain.user.application.command.LoginCommand;
import com.lion._iozoo.domain.user.application.command.SignupCommand;
import com.lion._iozoo.domain.user.application.command.UpdateProfileCommand;
import com.lion._iozoo.domain.user.application.port.out.LoadUserPort;
import com.lion._iozoo.domain.user.application.port.out.SaveUserPort;
import com.lion._iozoo.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final SaveUserPort saveUserPort;
    private final LoadUserPort loadUserPort;

    @Transactional
    public User signup(SignupCommand command) {
        // 1. 도메인 객체 생성 (비밀번호 인코딩 등은 여기서 처리하거나 도메인 내부에서 처리)
        User newUser = User.builder()
                .email(command.email())
                .password(command.password()) // 추후 Spring Security 적용 시 인코딩 필요
                .name(command.name())
                .build();

        // 2. Port를 통해 외부 인프라(DB)로 저장 위임
        return saveUserPort.saveUser(newUser);
    }

    @Transactional(readOnly = true)
    public User login(LoginCommand command) {
        // 1. 포트를 통해 DB에서 이메일로 회원 조회
        User user = loadUserPort.loadUserByEmail(command.email())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        // 2. 비밀번호 검증 (실제로는 암호화 검증 로직이 들어가야 함)
        if (!user.getPassword().equals(command.password())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }

    // UserService 파일 맨 아래에 다음 메서드를 추가합니다.
    @Transactional
    public User updateProfile(Long userId, UpdateProfileCommand command) {
        // 1. ID로 기존 유저 조회
        User user = loadUserPort.loadUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 도메인 객체의 이름, 시간대, 선호 언어 변경
        user.updateProfile(command.name(), command.timezone(), command.language());

        // 3. 변경된 객체를 다시 저장 (JPA가 알아서 UPDATE 쿼리를 날려줍니다)
        return saveUserPort.saveUser(user);
    }
}