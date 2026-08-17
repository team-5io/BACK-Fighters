package com.lion._iozoo.document.infrastructure.persistence.repository;

import com.lion._iozoo.document.infrastructure.persistence.entity.DocumentRaciEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRaciJpaRepository extends JpaRepository<DocumentRaciEntity, Long> {
    List<DocumentRaciEntity> findByDocumentId(Long documentId);

    void deleteByDocumentId(Long documentId);
}
