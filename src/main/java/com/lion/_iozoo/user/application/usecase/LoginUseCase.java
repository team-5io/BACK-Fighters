package com.lion._iozoo.user.application.usecase;

import com.lion._iozoo.user.application.command.LoginCommand;
import com.lion._iozoo.user.application.result.LoginResult;

public interface LoginUseCase {
    LoginResult login(LoginCommand command);
}
