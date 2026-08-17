package com.lion._iozoo.document.application.usecase;

import com.lion._iozoo.document.application.result.DocumentRelationExploreResult;

import java.util.List;

public interface GetDocumentRelationsUseCase {
    List<DocumentRelationExploreResult> explore(Long userId, Long documentId);
}
