package com.lion._iozoo.docpr.infrastructure.persistence;

import com.lion._iozoo.docpr.application.port.out.SaveDocPrReviewPort;
import com.lion._iozoo.docpr.application.result.DocPrReview;
import com.lion._iozoo.docpr.infrastructure.persistence.entity.DocPrReviewEntity;
import com.lion._iozoo.docpr.infrastructure.persistence.repository.DocPrReviewJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DocPrReviewPersistenceAdapter implements SaveDocPrReviewPort {

    private final DocPrReviewJpaRepository docPrReviewJpaRepository;

    @Override
    public DocPrReview save(Long docPrId, Long reviewerId, String comment) {
        DocPrReviewEntity saved = docPrReviewJpaRepository.save(
                DocPrReviewEntity.builder()
                        .docPrId(docPrId)
                        .reviewerId(reviewerId)
                        .comment(comment)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        return new DocPrReview(saved.getId(), saved.getDocPrId(), saved.getReviewerId(), saved.getComment(), saved.getCreatedAt());
    }
}
