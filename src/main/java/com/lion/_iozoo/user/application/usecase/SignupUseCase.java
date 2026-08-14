package com.lion._iozoo.user.application.usecase;

import com.lion._iozoo.user.application.command.SignupCommand;
import com.lion._iozoo.user.domain.User;

public interface SignupUseCase {
    User signup(SignupCommand command);
}
