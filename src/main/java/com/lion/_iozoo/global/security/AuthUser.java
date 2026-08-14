package com.lion._iozoo.global.security;

import org.springframework.security.core.AuthenticatedPrincipal;

public record AuthUser(Long userId) implements AuthenticatedPrincipal {

    @Override
    public String getName() {
        return String.valueOf(userId);
    }
}
