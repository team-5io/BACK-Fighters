package com.lion._iozoo.document.application.command;

import com.lion._iozoo.document.domain.RelationType;

public record CreateDocumentRelationCommand(
        Long targetDocumentId,
        RelationType relationType
) {
}
