package com.lion._iozoo.document.infrastructure.persistence.repository;

import com.lion._iozoo.document.infrastructure.persistence.entity.DocumentRelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRelationJpaRepository extends JpaRepository<DocumentRelationEntity, Long> {
}
