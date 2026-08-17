package com.lion._iozoo.team.application.port.out;

import java.util.Optional;

public interface LoadUserIdByEmailPort {
    Optional<Long> loadUserIdByEmail(String email);
}
