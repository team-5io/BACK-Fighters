package com.lion._iozoo.document.infrastructure.persistence.repository;

import com.lion._iozoo.document.infrastructure.persistence.entity.TranslationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TranslationJpaRepository extends JpaRepository<TranslationEntity, Long> {
    Optional<TranslationEntity> findByDocumentIdAndBlockIdAndTargetLanguage(Long documentId, String blockId, String targetLanguage);
}
