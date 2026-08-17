package com.lion._iozoo.docpr.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DocPr {
    private final Long id;
    private final Long documentId;
    private final Long requesterId;
    private Long approverId;
    private String proposedContent;
    private DocPrStatus status;
    private LocalDateTime mergedAt;
    private boolean exceptionMerge;
    private String exceptionReason;

    @Builder
    private DocPr(Long id, Long documentId, Long requesterId, Long approverId,
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

    public boolean isTerminal() {
        return this.status == DocPrStatus.APPROVED
                || this.status == DocPrStatus.REJECTED
                || this.status == DocPrStatus.MERGED;
    }

    public void reject() {
        this.status = DocPrStatus.REJECTED;
    }

    public void approve() {
        this.status = DocPrStatus.APPROVED;
    }

    public boolean isRejected() {
        return this.status == DocPrStatus.REJECTED;
    }

    public void resubmit(String proposedContent) {
        this.proposedContent = proposedContent;
        this.status = DocPrStatus.RESUBMITTED;
    }

    public boolean isApproved() {
        return this.status == DocPrStatus.APPROVED;
    }

    public void merge(LocalDateTime mergedAt) {
        this.status = DocPrStatus.MERGED;
        this.mergedAt = mergedAt;
    }

    public void changeApprover(Long newApproverId) {
        this.approverId = newApproverId;
    }

    public void mergeWithException(LocalDateTime mergedAt, String reason) {
        this.status = DocPrStatus.MERGED;
        this.mergedAt = mergedAt;
        this.exceptionMerge = true;
        this.exceptionReason = reason;
    }
}
