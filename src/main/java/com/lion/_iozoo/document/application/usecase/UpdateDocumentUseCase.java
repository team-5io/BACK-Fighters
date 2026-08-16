package com.lion._iozoo.document.application.usecase;

import com.lion._iozoo.document.application.command.UpdateDocumentCommand;
import com.lion._iozoo.document.domain.Document;

public interface UpdateDocumentUseCase {
    Document update(Long userId, Long documentId, UpdateDocumentCommand command);
}
