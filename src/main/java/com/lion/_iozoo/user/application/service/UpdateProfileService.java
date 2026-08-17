package com.lion._iozoo.user.application.service;

import com.lion._iozoo.user.application.command.UpdateProfileCommand;
import com.lion._iozoo.user.application.port.out.LoadUserPort;
import com.lion._iozoo.user.application.port.out.SaveUserPort;
import com.lion._iozoo.user.application.usecase.UpdateProfileUseCase;
import com.lion._iozoo.user.domain.User;
import com.lion._iozoo.user.domain.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateProfileService implements UpdateProfileUseCase {

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;

    @Override
    @Transactional
    public User updateProfile(Long userId, UpdateProfileCommand command) {
        log.info("event=user_profile_update_시작 userId={}", userId);

        try {
            // 1. ID로 기존 유저 조회
            User user = loadUserPort.loadUserById(userId)
                    .orElseThrow(UserNotFoundException::new);

            // 2. 도메인 객체의 이름, 시간대, 선호 언어 변경
            user.updateProfile(command.name(), command.timezone(), command.language());

            // 3. 변경된 객체를 다시 저장 (JPA가 알아서 UPDATE 쿼리를 날려줍니다)
            User saved = saveUserPort.saveUser(user);

            log.info("event=user_profile_update_완료 userId={}", userId);
            return saved;
        } catch (RuntimeException e) {
            log.warn("event=user_profile_update_실패 userId={}, reason={}", userId, e.getMessage(), e);
            throw e;
        }
    }
}
