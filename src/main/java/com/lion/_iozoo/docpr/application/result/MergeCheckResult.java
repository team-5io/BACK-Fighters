package com.lion._iozoo.docpr.application.result;

public record MergeCheckResult(
        boolean mergeable,
        String reason
) {
}
