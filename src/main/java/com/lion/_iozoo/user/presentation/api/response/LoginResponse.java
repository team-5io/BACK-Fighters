package com.lion._iozoo.user.presentation.api.response;

import com.lion._iozoo.user.domain.User;
import lombok.Builder;

import java.util.UUID;

@Builder
public record LoginResponse(
        UUID publicId,
        String email,
        String name,
        String accessToken
) {
    public static LoginResponse of(User user, String accessToken) {
        return LoginResponse.builder()
                .publicId(user.getPublicId())
                .email(user.getEmail())
                .name(user.getName())
                .accessToken(accessToken)
                .build();
    }
}