package com.lion._iozoo.document.infrastructure.persistence;

import com.lion._iozoo.docpr.application.port.out.CheckDocumentAccessPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentRaciPort;
import com.lion._iozoo.document.application.result.DocumentRaciEntry;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentAccessLevel;
import com.lion._iozoo.document.domain.RaciRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DocumentAccessAdapterForDocPr implements CheckDocumentAccessPort {

    private final LoadDocumentPort loadDocumentPort;
    private final LoadDocumentRaciPort loadDocumentRaciPort;

    /**
     * Consumer: docpr
     * Purpose: Doc PR 상세/이력/리뷰 의견 조회 시 문서의 RACI 접근수준이 FULL인지 확인 (I·역할없음은 차단)
     */
    @Override
    public boolean hasFullAccess(Long documentId, Long userId) {
        return loadDocumentPort.loadById(documentId)
                .map(document -> document.resolveAccessLevel(userId, roleOf(documentId, userId)) == DocumentAccessLevel.FULL)
                .orElse(false);
    }

    private RaciRole roleOf(Long documentId, Long userId) {
        List<DocumentRaciEntry> entries = loadDocumentRaciPort.loadByDocumentId(documentId);
        return entries.stream()
                .filter(entry -> entry.userId().equals(userId))
                .map(DocumentRaciEntry::role)
                .findFirst()
                .orElse(null);
    }
}
