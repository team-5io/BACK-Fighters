package com.lion._iozoo.docpr.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "doc_pr_reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DocPrReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "doc_pr_id", nullable = false)
    private Long docPrId;

    @Column(name = "reviewer_id", nullable = false)
    private Long reviewerId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String comment;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    private DocPrReviewEntity(Long id, Long docPrId, Long reviewerId, String comment, LocalDateTime createdAt) {
        this.id = id;
        this.docPrId = docPrId;
        this.reviewerId = reviewerId;
        this.comment = comment;
        this.createdAt = createdAt;
    }
}
