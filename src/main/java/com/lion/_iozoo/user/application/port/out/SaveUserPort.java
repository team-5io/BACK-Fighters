package com.lion._iozoo.user.application.port.out;

import com.lion._iozoo.user.domain.User;

public interface SaveUserPort {
    User saveUser(User user);
}