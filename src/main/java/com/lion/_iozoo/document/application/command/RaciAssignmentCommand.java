package com.lion._iozoo.document.application.command;

import com.lion._iozoo.document.domain.RaciRole;

public record RaciAssignmentCommand(
        Long memberId,
        RaciRole role
) {
}
