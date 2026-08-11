package com.lion._iozoo.domain.user.application.port.out;

import com.lion._iozoo.domain.user.domain.User;

public interface SaveUserPort {
    User saveUser(User user);
}