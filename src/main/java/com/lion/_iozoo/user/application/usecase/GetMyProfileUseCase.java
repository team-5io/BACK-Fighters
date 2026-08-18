package com.lion._iozoo.user.application.usecase;

import com.lion._iozoo.user.domain.User;

public interface GetMyProfileUseCase {
    User getMyProfile(Long userId);
}
