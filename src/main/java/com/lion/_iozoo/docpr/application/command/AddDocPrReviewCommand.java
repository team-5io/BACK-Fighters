package com.lion._iozoo.docpr.application.command;

public record AddDocPrReviewCommand(
        Long docPrId,
        String comment
) {
}
