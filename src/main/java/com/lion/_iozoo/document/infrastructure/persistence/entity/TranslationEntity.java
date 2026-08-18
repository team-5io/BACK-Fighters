package com.lion._iozoo.document.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "translations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TranslationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @Column(name = "block_id", nullable = false, length = 64)
    private String blockId;

    @Column(name = "source_language", nullable = false)
    private String sourceLanguage;

    @Column(name = "target_language", nullable = false)
    private String targetLanguage;

    @Column(name = "translated_content", columnDefinition = "LONGTEXT", nullable = false)
    private String translatedContent;

    // 원문 보존 코드 토큰 목록을 콤마로 이어붙여 저장 (도메인에서는 List<String>으로 변환).
    @Column(name = "preserved_terms", columnDefinition = "TEXT")
    private String preservedTerms;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    private TranslationEntity(Long id, Long documentId, String blockId, String sourceLanguage, String targetLanguage,
                              String translatedContent, String preservedTerms, LocalDateTime createdAt) {
        this.id = id;
        this.documentId = documentId;
        this.blockId = blockId;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.translatedContent = translatedContent;
        this.preservedTerms = preservedTerms;
        this.createdAt = createdAt;
    }
}
