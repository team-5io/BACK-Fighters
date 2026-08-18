package com.lion._iozoo.docpr.infrastructure.persistence.repository;

import com.lion._iozoo.docpr.infrastructure.persistence.entity.AiReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiReviewJpaRepository extends JpaRepository<AiReviewEntity, Long> {
    Optional<AiReviewEntity> findByDocPrId(Long docPrId);
}
