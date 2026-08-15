package com.lion._iozoo.user.application.result;

import com.lion._iozoo.user.domain.User;

public record LoginResult(User user, String accessToken) {
}
