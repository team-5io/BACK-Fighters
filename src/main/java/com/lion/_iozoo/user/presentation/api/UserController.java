package com.lion._iozoo.user.presentation.api;

import com.lion._iozoo.user.application.UserService;
import com.lion._iozoo.user.application.command.UpdateProfileCommand;
import com.lion._iozoo.user.domain.User;
import com.lion._iozoo.user.presentation.api.common.UserResponseCode;
import com.lion._iozoo.user.presentation.api.request.UpdateProfileRequest;
import com.lion._iozoo.user.presentation.api.response.UpdateProfileResponse;
import com.lion._iozoo.global.presentation.GlobalApiResponse;
import com.lion._iozoo.global.security.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/me")
    public GlobalApiResponse<UpdateProfileResponse> updateProfile(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody @Valid UpdateProfileRequest request) {

        UpdateProfileCommand command = new UpdateProfileCommand(
                request.name(),
                request.timezone(),
                request.language()
        );

        User updatedUser = userService.updateProfile(authUser.userId(), command);

        return GlobalApiResponse.ok(UserResponseCode.PROFILE_UPDATED, UpdateProfileResponse.from(updatedUser));
    }
}