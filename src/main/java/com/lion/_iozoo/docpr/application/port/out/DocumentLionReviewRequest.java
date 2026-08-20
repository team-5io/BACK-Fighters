package com.lion._iozoo.docpr.application.port.out;

import java.util.List;
import java.util.UUID;

public record DocumentLionReviewRequest(
        Long documentId,
        Long docPrId,
        Long teamId,
        UUID requestedBy,
        String content,
        List<DocumentBlockContent> blocks,
        List<RelatedDocumentContent> relatedDocuments) {
}
