package com.lion._iozoo.team.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record InviteTeamMemberRequest(
        @Schema(description = "초대할 가입 유저의 이메일", example = "invitee@example.com")
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email
) {
}
