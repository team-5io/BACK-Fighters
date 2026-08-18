package com.lion._iozoo.document.application.command;

public record RequestTranslationCommand(String content, String sourceLanguage, String targetLanguage) {
}
