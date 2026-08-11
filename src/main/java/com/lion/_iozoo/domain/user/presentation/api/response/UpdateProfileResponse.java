package com.lion._iozoo.domain.user.presentation.api.response;

import com.lion._iozoo.domain.user.domain.User;
import lombok.Builder;

@Builder
public record UpdateProfileResponse(
        Long id,
        String email,
        String name,
        String timezone,
        String language
) {
    public static UpdateProfileResponse from(User user) {
        return UpdateProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .timezone(user.getTimezone())
                .language(user.getLanguage())
                .build();
    }
}