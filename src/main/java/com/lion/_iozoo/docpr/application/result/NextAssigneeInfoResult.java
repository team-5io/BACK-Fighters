package com.lion._iozoo.docpr.application.result;

import com.lion._iozoo.docpr.domain.DocPrStatus;

public record NextAssigneeInfoResult(
        Long docPrId,
        DocPrStatus status,
        boolean needsNextAssignee,
        Long nextAssigneeId,
        DocPrHistoryEntry latestHandoff
) {
}
