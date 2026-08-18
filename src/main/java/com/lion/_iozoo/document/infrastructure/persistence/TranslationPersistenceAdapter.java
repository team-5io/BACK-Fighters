package com.lion._iozoo.document.infrastructure.persistence;

import com.lion._iozoo.document.application.port.out.LoadCachedTranslationPort;
import com.lion._iozoo.document.application.port.out.LoadTranslationPort;
import com.lion._iozoo.document.application.port.out.SaveTranslationPort;
import com.lion._iozoo.document.domain.Translation;
import com.lion._iozoo.document.infrastructure.persistence.entity.TranslationEntity;
import com.lion._iozoo.document.infrastructure.persistence.repository.TranslationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TranslationPersistenceAdapter implements LoadCachedTranslationPort, SaveTranslationPort, LoadTranslationPort {

    private static final String PRESERVED_TERMS_DELIMITER = ",";

    private final TranslationJpaRepository translationJpaRepository;

    @Override
    public Optional<Translation> loadByDocumentIdAndTargetLanguage(Long documentId, String targetLanguage) {
        return translationJpaRepository.findByDocumentIdAndTargetLanguage(documentId, targetLanguage)
                .map(this::toDomain);
    }

    @Override
    public Translation save(Translation translation) {
        TranslationEntity entity = TranslationEntity.builder()
                .id(translation.getId())
                .documentId(translation.getDocumentId())
                .sourceLanguage(translation.getSourceLanguage())
                .targetLanguage(translation.getTargetLanguage())
                .translatedContent(translation.getTranslatedContent())
                .preservedTerms(joinPreservedTerms(translation.getPreservedTerms()))
                .createdAt(translation.getCreatedAt())
                .build();

        TranslationEntity saved = translationJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Translation> loadById(Long translationId) {
        return translationJpaRepository.findById(translationId)
                .map(this::toDomain);
    }

    private Translation toDomain(TranslationEntity entity) {
        return Translation.builder()
                .id(entity.getId())
                .documentId(entity.getDocumentId())
                .sourceLanguage(entity.getSourceLanguage())
                .targetLanguage(entity.getTargetLanguage())
                .translatedContent(entity.getTranslatedContent())
                .preservedTerms(splitPreservedTerms(entity.getPreservedTerms()))
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private String joinPreservedTerms(List<String> preservedTerms) {
        if (preservedTerms == null || preservedTerms.isEmpty()) {
            return null;
        }
        return String.join(PRESERVED_TERMS_DELIMITER, preservedTerms);
    }

    private List<String> splitPreservedTerms(String preservedTerms) {
        if (preservedTerms == null || preservedTerms.isBlank()) {
            return List.of();
        }
        return List.of(preservedTerms.split(PRESERVED_TERMS_DELIMITER));
    }
}
