package com.lion._iozoo.docpr.presentation.api.response;

import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.domain.DocPrStatus;
import lombok.Builder;

@Builder
public record DocPrResponse(
        Long id,
        Long documentId,
        Long requesterId,
        Long approverId,
        String proposedContent,
        DocPrStatus status
) {
    public static DocPrResponse from(DocPr docPr) {
        return DocPrResponse.builder()
                .id(docPr.getId())
                .documentId(docPr.getDocumentId())
                .requesterId(docPr.getRequesterId())
                .approverId(docPr.getApproverId())
                .proposedContent(docPr.getProposedContent())
                .status(docPr.getStatus())
                .build();
    }
}
