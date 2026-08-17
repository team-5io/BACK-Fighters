package com.lion._iozoo.document.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DocumentRelation {
    private final Long id;
    private final Long sourceDocumentId;
    private final Long targetDocumentId;
    private final RelationType relationType;
    private final LocalDateTime createdAt;

    @Builder
    private DocumentRelation(Long id, Long sourceDocumentId, Long targetDocumentId,
                              RelationType relationType, LocalDateTime createdAt) {
        this.id = id;
        this.sourceDocumentId = sourceDocumentId;
        this.targetDocumentId = targetDocumentId;
        this.relationType = relationType;
        this.createdAt = createdAt;
    }
}
