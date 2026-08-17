package com.lion._iozoo.document.application.result;

import com.lion._iozoo.document.domain.RelationDirection;
import com.lion._iozoo.document.domain.RelationType;

import java.time.LocalDateTime;

public record DocumentRelationExploreResult(
        Long relationId,
        RelationDirection direction,
        RelationType relationType,
        Long neighborDocumentId,
        String neighborTitle,
        LocalDateTime createdAt
) {
}
