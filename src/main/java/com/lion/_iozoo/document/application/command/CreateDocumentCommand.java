package com.lion._iozoo.document.application.command;

public record CreateDocumentCommand(
        Long teamId,
        String title,
        String content
) {
}
