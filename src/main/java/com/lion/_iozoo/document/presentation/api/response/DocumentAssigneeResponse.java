package com.lion._iozoo.document.presentation.api.response;

import com.lion._iozoo.document.application.port.out.UserSummary;
import com.lion._iozoo.document.domain.RaciRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record DocumentAssigneeResponse(
        @Schema(description = "담당자 사용자 ID", example = "10")
        Long userId,

        @Schema(description = "담당자 이름", example = "김재원")
        String name,

        @Schema(description = "문서 내 역할 (작성자는 항상 R)", example = "R")
        RaciRole role
) {
    public static DocumentAssigneeResponse of(Long userId, UserSummary summary) {
        return DocumentAssigneeResponse.builder()
                .userId(userId)
                .name(summary == null ? null : summary.name())
                .role(RaciRole.R)
                .build();
    }
}
