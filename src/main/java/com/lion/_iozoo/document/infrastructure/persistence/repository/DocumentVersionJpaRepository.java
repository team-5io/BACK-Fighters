package com.lion._iozoo.document.infrastructure.persistence.repository;

import com.lion._iozoo.document.infrastructure.persistence.entity.DocumentVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentVersionJpaRepository extends JpaRepository<DocumentVersionEntity, Long> {

    List<DocumentVersionEntity> findByDocumentIdOrderByVersionNoAsc(Long documentId);

    int countByDocumentId(Long documentId);
}
