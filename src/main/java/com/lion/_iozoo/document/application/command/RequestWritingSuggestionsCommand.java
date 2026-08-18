package com.lion._iozoo.document.application.command;

public record RequestWritingSuggestionsCommand(String content, String cursorContext) {
}
