package com.lion._iozoo.document.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DocumentVersion {
    private final Long id;
    private final Long documentId;
    private final int versionNo;
    private final String content;
    private final Long docPrId;
    private final LocalDateTime createdAt;

    @Builder
    private DocumentVersion(Long id, Long documentId, int versionNo, String content,
                             Long docPrId, LocalDateTime createdAt) {
        this.id = id;
        this.documentId = documentId;
        this.versionNo = versionNo;
        this.content = content;
        this.docPrId = docPrId;
        this.createdAt = createdAt;
    }
}
