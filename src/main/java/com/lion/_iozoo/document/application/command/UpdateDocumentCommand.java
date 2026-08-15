package com.lion._iozoo.document.application.command;

public record UpdateDocumentCommand(
        String title,
        String content
) {
}
