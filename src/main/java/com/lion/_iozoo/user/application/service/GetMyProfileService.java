package com.lion._iozoo.user.application.service;

import com.lion._iozoo.user.application.port.out.LoadUserPort;
import com.lion._iozoo.user.application.usecase.GetMyProfileUseCase;
import com.lion._iozoo.user.domain.User;
import com.lion._iozoo.user.domain.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetMyProfileService implements GetMyProfileUseCase {

    private final LoadUserPort loadUserPort;

    @Override
    @Transactional(readOnly = true)
    public User getMyProfile(Long userId) {
        return loadUserPort.loadUserById(userId)
                .orElseThrow(UserNotFoundException::new);
    }
}
