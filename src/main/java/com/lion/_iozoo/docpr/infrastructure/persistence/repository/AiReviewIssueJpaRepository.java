package com.lion._iozoo.docpr.infrastructure.persistence.repository;

import com.lion._iozoo.docpr.domain.AiReviewIssueStatus;
import com.lion._iozoo.docpr.infrastructure.persistence.entity.AiReviewIssueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiReviewIssueJpaRepository extends JpaRepository<AiReviewIssueEntity, Long> {
    List<AiReviewIssueEntity> findByDocPrId(Long docPrId);
    List<AiReviewIssueEntity> findByDocPrIdAndStatus(Long docPrId, AiReviewIssueStatus status);
}
