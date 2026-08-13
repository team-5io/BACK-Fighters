package com.lion._iozoo.user.presentation.api.response;

import com.lion._iozoo.user.domain.User;
import lombok.Builder;

@Builder
public record LoginResponse(
        Long id,
        String email,
        String name,
        String accessToken
) {
    public static LoginResponse of(User user, String accessToken) {
        return LoginResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .accessToken(accessToken)
                .build();
    }
}