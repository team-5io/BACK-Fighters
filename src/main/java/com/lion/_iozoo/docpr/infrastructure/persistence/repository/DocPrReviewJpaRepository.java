package com.lion._iozoo.docpr.infrastructure.persistence.repository;

import com.lion._iozoo.docpr.infrastructure.persistence.entity.DocPrReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocPrReviewJpaRepository extends JpaRepository<DocPrReviewEntity, Long> {
}
