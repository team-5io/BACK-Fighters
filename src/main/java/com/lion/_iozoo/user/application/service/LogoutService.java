package com.lion._iozoo.user.application.service;

import com.lion._iozoo.global.security.JwtBlacklist;
import com.lion._iozoo.user.application.usecase.LogoutUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutUseCase {

    private final JwtBlacklist jwtBlacklist;

    @Override
    public void logout(String accessToken) {
        // 토큰 값 자체는 민감정보라 로그에 남기지 않는다.
        log.info("event=user_logout_시작");

        try {
            jwtBlacklist.add(accessToken);
            log.info("event=user_logout_완료");
        } catch (RuntimeException e) {
            log.warn("event=user_logout_실패 reason={}", e.getMessage(), e);
            throw e;
        }
    }
}
