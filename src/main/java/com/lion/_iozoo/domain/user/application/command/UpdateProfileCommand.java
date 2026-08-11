package com.lion._iozoo.domain.user.application.command;

public record UpdateProfileCommand(
        String name,
        String timezone,
        String language
) {
}