package com.lion._iozoo.docpr.infrastructure.persistence;

import com.lion._iozoo.docpr.application.port.out.LoadAiReviewPort;
import com.lion._iozoo.docpr.application.port.out.SaveAiReviewPort;
import com.lion._iozoo.docpr.domain.AiReview;
import com.lion._iozoo.docpr.infrastructure.persistence.entity.AiReviewEntity;
import com.lion._iozoo.docpr.infrastructure.persistence.repository.AiReviewJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AiReviewPersistenceAdapter implements LoadAiReviewPort, SaveAiReviewPort {

    private final AiReviewJpaRepository aiReviewJpaRepository;

    @Override
    public Optional<AiReview> loadByDocPrId(Long docPrId) {
        return aiReviewJpaRepository.findByDocPrId(docPrId).map(this::toDomain);
    }

    @Override
    public AiReview saveOrReplace(AiReview aiReview) {
        AiReviewEntity entity = aiReviewJpaRepository.findByDocPrId(aiReview.getDocPrId())
                .orElseGet(() -> AiReviewEntity.builder().docPrId(aiReview.getDocPrId()).build());

        entity.setHasConflict(aiReview.isHasConflict());
        entity.setConsistent(aiReview.isConsistent());
        entity.setViolatesCharter(aiReview.isViolatesCharter());
        entity.setEvidence(aiReview.getEvidence());
        entity.setReviewedAt(aiReview.getReviewedAt());

        AiReviewEntity saved = aiReviewJpaRepository.save(entity);
        return toDomain(saved);
    }

    private AiReview toDomain(AiReviewEntity entity) {
        return AiReview.builder()
                .id(entity.getId())
                .docPrId(entity.getDocPrId())
                .hasConflict(entity.isHasConflict())
                .isConsistent(entity.isConsistent())
                .violatesCharter(entity.isViolatesCharter())
                .evidence(entity.getEvidence())
                .reviewedAt(entity.getReviewedAt())
                .build();
    }
}
