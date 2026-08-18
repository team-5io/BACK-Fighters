package com.lion._iozoo.user.presentation.api;

import com.lion._iozoo.user.application.command.UpdateProfileCommand;
import com.lion._iozoo.user.application.usecase.GetMyProfileUseCase;
import com.lion._iozoo.user.application.usecase.UpdateProfileUseCase;
import com.lion._iozoo.user.domain.User;
import com.lion._iozoo.user.presentation.api.common.UserResponseCode;
import com.lion._iozoo.user.presentation.api.request.UpdateProfileRequest;
import com.lion._iozoo.user.presentation.api.response.UpdateProfileResponse;
import com.lion._iozoo.global.presentation.GlobalApiResponse;
import com.lion._iozoo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "개인 프로필 관리 API")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UpdateProfileUseCase updateProfileUseCase;
    private final GetMyProfileUseCase getMyProfileUseCase;

    @Operation(summary = "내 정보 조회", description = "로그인한 유저의 프로필 정보를 조회한다.")
    @GetMapping("/me")
    public GlobalApiResponse<UpdateProfileResponse> getMyProfile(@AuthenticationPrincipal AuthUser authUser) {
        User user = getMyProfileUseCase.getMyProfile(authUser.userId());

        return GlobalApiResponse.ok(UserResponseCode.PROFILE_FETCHED, UpdateProfileResponse.from(user));
    }

    @Operation(summary = "개인 프로필 설정", description = "로그인한 유저의 이름/시간대/선호 언어를 변경한다.")
    @PatchMapping("/me")
    public GlobalApiResponse<UpdateProfileResponse> updateProfile(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody @Valid UpdateProfileRequest request) {

        UpdateProfileCommand command = new UpdateProfileCommand(
                request.name(),
                request.timezone(),
                request.language()
        );

        User updatedUser = updateProfileUseCase.updateProfile(authUser.userId(), command);

        return GlobalApiResponse.ok(UserResponseCode.PROFILE_UPDATED, UpdateProfileResponse.from(updatedUser));
    }
}