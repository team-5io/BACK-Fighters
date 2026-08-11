package com.lion._iozoo.domain.user.presentation.api;

import com.lion._iozoo.domain.user.application.UserService;
import com.lion._iozoo.domain.user.application.command.UpdateProfileCommand;
import com.lion._iozoo.domain.user.domain.User;
import com.lion._iozoo.domain.user.presentation.api.request.UpdateProfileRequest;
import com.lion._iozoo.domain.user.presentation.api.response.UpdateProfileResponse;
import com.lion._iozoo.global.presentation.GlobalApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/me")
    public GlobalApiResponse<UpdateProfileResponse> updateProfile(
            // 임시로 Headers에 X-User-Id 라는 이름으로 내 ID 받기
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody @Valid UpdateProfileRequest request) {

        UpdateProfileCommand command = new UpdateProfileCommand(
                request.name(),
                request.timezone(),
                request.language()
        );

        User updatedUser = userService.updateProfile(userId, command);

        return GlobalApiResponse.ok(UpdateProfileResponse.from(updatedUser));
    }
}