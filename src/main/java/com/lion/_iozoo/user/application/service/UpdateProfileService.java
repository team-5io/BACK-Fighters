package com.lion._iozoo.user.application.service;

import com.lion._iozoo.user.application.command.UpdateProfileCommand;
import com.lion._iozoo.user.application.port.out.LoadUserPort;
import com.lion._iozoo.user.application.port.out.SaveUserPort;
import com.lion._iozoo.user.application.usecase.UpdateProfileUseCase;
import com.lion._iozoo.user.domain.User;
import com.lion._iozoo.user.domain.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateProfileService implements UpdateProfileUseCase {

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;

    @Override
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
