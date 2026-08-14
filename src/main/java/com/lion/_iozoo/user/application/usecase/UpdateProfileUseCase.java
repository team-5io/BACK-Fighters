package com.lion._iozoo.user.application.usecase;

import com.lion._iozoo.user.application.command.UpdateProfileCommand;
import com.lion._iozoo.user.domain.User;

public interface UpdateProfileUseCase {
    User updateProfile(Long userId, UpdateProfileCommand command);
}
