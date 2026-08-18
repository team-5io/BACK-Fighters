package com.lion._iozoo.docpr.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AiReview {
    private final Long id;
    private final Long docPrId;
    private final boolean hasConflict;
    private final boolean isConsistent;
    private final boolean violatesCharter;
    private final String evidence;
    private final LocalDateTime reviewedAt;

    @Builder
    private AiReview(Long id, Long docPrId, boolean hasConflict, boolean isConsistent,
                      boolean violatesCharter, String evidence, LocalDateTime reviewedAt) {
        this.id = id;
        this.docPrId = docPrId;
        this.hasConflict = hasConflict;
        this.isConsistent = isConsistent;
        this.violatesCharter = violatesCharter;
        this.evidence = evidence;
        this.reviewedAt = reviewedAt;
    }
}
