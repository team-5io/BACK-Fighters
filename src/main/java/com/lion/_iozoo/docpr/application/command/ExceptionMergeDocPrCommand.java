package com.lion._iozoo.docpr.application.command;

public record ExceptionMergeDocPrCommand(
        Long docPrId,
        String reason
) {
}
