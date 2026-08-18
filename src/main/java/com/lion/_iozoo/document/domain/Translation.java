package com.lion._iozoo.document.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class Translation {
    private final Long id;
    private final Long documentId;
    private final String sourceLanguage;
    private final String targetLanguage;
    private final String translatedContent;
    private final List<String> preservedTerms;
    private final LocalDateTime createdAt;

    @Builder
    private Translation(Long id, Long documentId, String sourceLanguage, String targetLanguage,
                         String translatedContent, List<String> preservedTerms, LocalDateTime createdAt) {
        this.id = id;
        this.documentId = documentId;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.translatedContent = translatedContent;
        this.preservedTerms = preservedTerms;
        this.createdAt = createdAt;
    }
}
