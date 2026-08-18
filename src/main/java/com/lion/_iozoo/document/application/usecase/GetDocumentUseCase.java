package com.lion._iozoo.document.application.usecase;

import com.lion._iozoo.document.domain.Document;

public interface GetDocumentUseCase {
    Document getById(Long userId, Long documentId);
}
