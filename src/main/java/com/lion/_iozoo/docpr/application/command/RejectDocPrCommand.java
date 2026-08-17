package com.lion._iozoo.docpr.application.command;

public record RejectDocPrCommand(
        Long docPrId,
        String reason
) {
}
