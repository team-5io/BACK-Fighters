package com.lion._iozoo.docpr.application.result;

import java.time.LocalDateTime;

public record DocPrReview(
        Long id,
        Long docPrId,
        Long reviewerId,
        String comment,
        LocalDateTime createdAt
) {
}
