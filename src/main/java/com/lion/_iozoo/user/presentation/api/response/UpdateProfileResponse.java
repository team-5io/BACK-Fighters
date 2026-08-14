package com.lion._iozoo.user.presentation.api.response;

import com.lion._iozoo.user.domain.User;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdateProfileResponse(
        UUID publicId,
        String email,
        String name,
        String timezone,
        String language
) {
    public static UpdateProfileResponse from(User user) {
        return UpdateProfileResponse.builder()
                .publicId(user.getPublicId())
                .email(user.getEmail())
                .name(user.getName())
                .timezone(user.getTimezone())
                .language(user.getLanguage())
                .build();
    }
}