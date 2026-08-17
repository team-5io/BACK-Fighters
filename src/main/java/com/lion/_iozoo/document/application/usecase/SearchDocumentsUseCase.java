package com.lion._iozoo.document.application.usecase;

import com.lion._iozoo.document.domain.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchDocumentsUseCase {
    Page<Document> search(Long userId, Long teamId, String keyword, Pageable pageable);
}
