package com.lion._iozoo.user.application.command;

public record LoginCommand(
        String email,
        String password
) {
}