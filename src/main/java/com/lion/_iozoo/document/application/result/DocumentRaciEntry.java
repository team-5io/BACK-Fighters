package com.lion._iozoo.document.application.result;

import com.lion._iozoo.document.domain.RaciRole;

import java.time.LocalDateTime;

public record DocumentRaciEntry(
        Long userId,
        RaciRole role,
        Long assignedBy,
        LocalDateTime assignedAt
) {
}
