package com.lion._iozoo.docpr.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DocPr {
    private final Long id;
    private final Long documentId;
    private final Long requesterId;
    private final Long approverId;
    private final String proposedContent;
    private DocPrStatus status;

    @Builder
    private DocPr(Long id, Long documentId, Long requesterId, Long approverId,
                  String proposedContent, DocPrStatus status) {
        this.id = id;
        this.documentId = documentId;
        this.requesterId = requesterId;
        this.approverId = approverId;
        this.proposedContent = proposedContent;
        this.status = status;
    }

    public boolean isTerminal() {
        return this.status == DocPrStatus.REJECTED || this.status == DocPrStatus.MERGED;
    }

    public void reject() {
        this.status = DocPrStatus.REJECTED;
    }
}
