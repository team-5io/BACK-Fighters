package com.lion._iozoo.domain.user.application.command;

public record SignupCommand(
        String email,
        String password,
        String name
) {
}