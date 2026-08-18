package com.lion._iozoo.docpr.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "doc_pr_id", nullable = false, unique = true)
    private Long docPrId;

    @Setter
    @Column(name = "has_conflict", nullable = false)
    private boolean hasConflict;

    @Setter
    @Column(name = "is_consistent", nullable = false)
    private boolean isConsistent;

    @Setter
    @Column(name = "violates_charter", nullable = false)
    private boolean violatesCharter;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String evidence;

    @Setter
    @Column(name = "reviewed_at", nullable = false)
    private LocalDateTime reviewedAt;

    @Builder
    private AiReviewEntity(Long id, Long docPrId, boolean hasConflict, boolean isConsistent,
                            boolean violatesCharter, String evidence, LocalDateTime reviewedAt) {
        this.id = id;
        this.docPrId = docPrId;
        this.hasConflict = hasConflict;
        this.isConsistent = isConsistent;
        this.violatesCharter = violatesCharter;
        this.evidence = evidence;
        this.reviewedAt = reviewedAt;
    }
}
