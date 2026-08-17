package com.lion._iozoo.docpr.application.command;

public record ResubmitDocPrCommand(
        Long docPrId,
        String proposedContent
) {
}
