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

    @Builder
    private DocPr(Long id, Long documentId, Long requesterId, Long approverId,
                  String proposedContent, DocPrStatus status, LocalDateTime mergedAt) {
        this.id = id;
        this.documentId = documentId;
        this.requesterId = requesterId;
        this.approverId = approverId;
        this.proposedContent = proposedContent;
        this.status = status;
        this.mergedAt = mergedAt;
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
}
