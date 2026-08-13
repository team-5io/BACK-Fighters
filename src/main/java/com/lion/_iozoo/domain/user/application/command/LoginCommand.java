package com.lion._iozoo.domain.user.application.command;

public record LoginCommand(
        String email,
        String password
) {
}