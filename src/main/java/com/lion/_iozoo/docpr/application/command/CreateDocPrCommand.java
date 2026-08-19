package com.lion._iozoo.docpr.application.command;

public record CreateDocPrCommand(
        Long documentId,
        Long approverMemberId,
        String proposedContent
) {
}
