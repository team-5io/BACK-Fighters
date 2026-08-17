package com.lion._iozoo.document.application.port.out;

import com.lion._iozoo.document.domain.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface LoadDocumentPort {
    Optional<Document> loadById(Long documentId);
    // restricted 문서는 작성자만 목록/검색 결과에서 볼 수 있도록 userId로 필터링한다.
    Page<Document> loadByTeamId(Long teamId, Long userId, Pageable pageable);
    Page<Document> searchByKeyword(Long teamId, Long userId, String keyword, Pageable pageable);
}
