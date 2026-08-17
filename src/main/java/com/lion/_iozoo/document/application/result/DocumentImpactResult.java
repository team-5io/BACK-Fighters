package com.lion._iozoo.document.application.result;

public record DocumentImpactResult(
        Long documentId,
        String title,
        int depth
) {
}
