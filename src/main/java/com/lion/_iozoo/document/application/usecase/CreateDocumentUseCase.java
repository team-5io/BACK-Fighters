package com.lion._iozoo.document.application.usecase;

import com.lion._iozoo.document.application.command.CreateDocumentCommand;
import com.lion._iozoo.document.domain.Document;

public interface CreateDocumentUseCase {
    Document create(Long userId, CreateDocumentCommand command);
}
