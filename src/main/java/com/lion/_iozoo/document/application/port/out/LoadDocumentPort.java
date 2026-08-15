package com.lion._iozoo.document.application.port.out;

import com.lion._iozoo.document.domain.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface LoadDocumentPort {
    Optional<Document> loadById(Long documentId);
    Page<Document> loadByTeamId(Long teamId, Pageable pageable);
    Page<Document> searchByKeyword(Long teamId, String keyword, Pageable pageable);
}
