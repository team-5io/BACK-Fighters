package com.lion._iozoo.document.infrastructure.persistence;

import com.lion._iozoo.docpr.application.port.out.DocumentSummary;
import com.lion._iozoo.docpr.application.port.out.LoadDocumentForDocPrPort;
import com.lion._iozoo.document.domain.DocumentStatus;
import com.lion._iozoo.document.infrastructure.persistence.repository.DocumentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DocumentQueryAdapterForDocPr implements LoadDocumentForDocPrPort {

    private final DocumentJpaRepository documentJpaRepository;

    /**
     * Consumer: docpr
     * Purpose: 초안 → Doc PR 전환 시 문서 작성자·팀·초안 상태 확인
     */
    @Override
    public Optional<DocumentSummary> loadSummary(Long documentId) {
        return documentJpaRepository.findById(documentId)
                .filter(entity -> entity.getDeletedAt() == null)
                .map(entity -> new DocumentSummary(
                        entity.getId(),
                        entity.getTeamId(),
                        entity.getAuthorId(),
                        entity.getStatus() == DocumentStatus.DRAFT
                ));
    }
}
