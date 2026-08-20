package com.lion._iozoo.docpr.application.result;

import java.util.List;

public record DocumentLionGatewayResult(
        boolean hasConflict, boolean isConsistent, boolean violatesCharter, String evidence,
        List<AiReviewIssueResult> issues) {
}
