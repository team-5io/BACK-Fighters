package com.lion._iozoo.user.application.command;

public record SignupCommand(
        String email,
        String password,
        String name
) {
}