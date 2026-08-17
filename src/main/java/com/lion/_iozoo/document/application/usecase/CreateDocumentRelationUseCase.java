package com.lion._iozoo.document.application.usecase;

import com.lion._iozoo.document.application.command.CreateDocumentRelationCommand;
import com.lion._iozoo.document.domain.DocumentRelation;

public interface CreateDocumentRelationUseCase {
    DocumentRelation create(Long userId, Long sourceDocumentId, CreateDocumentRelationCommand command);
}
