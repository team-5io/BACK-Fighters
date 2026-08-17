package com.lion._iozoo.document.infrastructure.persistence.repository;

import com.lion._iozoo.document.infrastructure.persistence.entity.DocumentRelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRelationJpaRepository extends JpaRepository<DocumentRelationEntity, Long> {

    List<DocumentRelationEntity> findBySourceDocumentIdOrTargetDocumentId(Long sourceDocumentId, Long targetDocumentId);
}
