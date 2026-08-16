package com.lion._iozoo.docpr.infrastructure.persistence.repository;

import com.lion._iozoo.docpr.infrastructure.persistence.entity.DocPrEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocPrJpaRepository extends JpaRepository<DocPrEntity, Long> {
}
