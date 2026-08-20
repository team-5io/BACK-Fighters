package com.lion._iozoo.document.application.usecase;

import com.lion._iozoo.document.application.result.DocumentWithRelationsResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetDocumentRelationsUseCase {
    Page<DocumentWithRelationsResult> explore(Long userId, Long teamId, Pageable pageable);
}
