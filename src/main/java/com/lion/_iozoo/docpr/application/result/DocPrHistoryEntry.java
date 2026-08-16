package com.lion._iozoo.docpr.application.result;

import com.lion._iozoo.docpr.domain.DocPrStatus;

import java.time.LocalDateTime;

public record DocPrHistoryEntry(
        DocPrStatus fromStatus,
        DocPrStatus toStatus,
        Long actorId,
        String reason,
        LocalDateTime createdAt
) {
}
