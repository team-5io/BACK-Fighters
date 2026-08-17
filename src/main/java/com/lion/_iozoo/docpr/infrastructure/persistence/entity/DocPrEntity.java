package com.lion._iozoo.docpr.infrastructure.persistence.entity;

import com.lion._iozoo.docpr.domain.DocPrStatus;
import com.lion._iozoo.global.infrastructure.persistence.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "doc_prs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DocPrEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @Column(name = "requester_id", nullable = false)
    private Long requesterId;

    @Column(name = "approver_id", nullable = false)
    private Long approverId;

    @Column(name = "proposed_content", columnDefinition = "LONGTEXT", nullable = false)
    private String proposedContent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocPrStatus status;

    @Column(name = "merged_at")
    private LocalDateTime mergedAt;

    @Column(name = "is_exception_merge", nullable = false)
    private boolean exceptionMerge;

    @Column(name = "exception_reason", length = 500)
    private String exceptionReason;

    @Builder
    private DocPrEntity(Long id, Long documentId, Long requesterId, Long approverId,
                        String proposedContent, DocPrStatus status, LocalDateTime mergedAt,
                        boolean exceptionMerge, String exceptionReason) {
        this.id = id;
        this.documentId = documentId;
        this.requesterId = requesterId;
        this.approverId = approverId;
        this.proposedContent = proposedContent;
        this.status = status;
        this.mergedAt = mergedAt;
        this.exceptionMerge = exceptionMerge;
        this.exceptionReason = exceptionReason;
    }
}
