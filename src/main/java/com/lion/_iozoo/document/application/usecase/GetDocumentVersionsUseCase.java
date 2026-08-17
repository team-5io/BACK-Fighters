package com.lion._iozoo.document.application.usecase;

import com.lion._iozoo.document.domain.DocumentVersion;

import java.util.List;

public interface GetDocumentVersionsUseCase {
    List<DocumentVersion> getVersions(Long userId, Long documentId);
}
