package com.lion._iozoo.user.application.command;

public record UpdateProfileCommand(
        String name,
        String timezone,
        String language
) {
}