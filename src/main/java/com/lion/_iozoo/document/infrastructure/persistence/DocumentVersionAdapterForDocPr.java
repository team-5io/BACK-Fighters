package com.lion._iozoo.document.infrastructure.persistence;

import com.lion._iozoo.docpr.application.port.out.RecordDocumentVersionPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentVersionsPort;
import com.lion._iozoo.document.application.port.out.SaveDocumentVersionPort;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentVersion;
import com.lion._iozoo.document.domain.exception.DocumentNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DocumentVersionAdapterForDocPr implements RecordDocumentVersionPort {

    private final LoadDocumentPort loadDocumentPort;
    private final LoadDocumentVersionsPort loadDocumentVersionsPort;
    private final SaveDocumentVersionPort saveDocumentVersionPort;

    /**
     * Consumer: docpr
     * Purpose: Doc PR Merge 확정 시 반영된 문서의 평문 캐시(content)를 새 버전 스냅샷으로 기록
     */
    @Override
    @Transactional
    public void record(Long documentId, Long docPrId) {
        Document document = loadDocumentPort.loadById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));
        int nextVersionNo = loadDocumentVersionsPort.countByDocumentId(documentId) + 1;

        saveDocumentVersionPort.save(DocumentVersion.builder()
                .documentId(documentId)
                .versionNo(nextVersionNo)
                .content(document.getContent())
                .docPrId(docPrId)
                .createdAt(LocalDateTime.now())
                .build());
    }
}
