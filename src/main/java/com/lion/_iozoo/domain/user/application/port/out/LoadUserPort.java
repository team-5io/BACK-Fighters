package com.lion._iozoo.domain.user.application.port.out;

import com.lion._iozoo.domain.user.domain.User;
import java.util.Optional;

public interface LoadUserPort {
    // 이메일로 유저를 찾기
    Optional<User> loadUserByEmail(String email);
    Optional<User> loadUserById(Long id);
}