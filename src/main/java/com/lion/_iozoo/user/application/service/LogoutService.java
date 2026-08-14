package com.lion._iozoo.user.application.service;

import com.lion._iozoo.global.security.JwtBlacklist;
import com.lion._iozoo.user.application.usecase.LogoutUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutUseCase {

    private final JwtBlacklist jwtBlacklist;

    @Override
    public void logout(String accessToken) {
        // 프론트엔드에서 보낸 토큰을 블랙리스트에 등록하여 무효화 처리
        jwtBlacklist.add(accessToken);
    }
}
