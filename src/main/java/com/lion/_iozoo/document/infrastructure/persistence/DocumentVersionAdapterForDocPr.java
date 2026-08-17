package com.lion._iozoo.document.infrastructure.persistence;

import com.lion._iozoo.docpr.application.port.out.RecordDocumentVersionPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentVersionsPort;
import com.lion._iozoo.document.application.port.out.SaveDocumentVersionPort;
import com.lion._iozoo.document.domain.DocumentVersion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DocumentVersionAdapterForDocPr implements RecordDocumentVersionPort {

    private final LoadDocumentVersionsPort loadDocumentVersionsPort;
    private final SaveDocumentVersionPort saveDocumentVersionPort;

    /**
     * Consumer: docpr
     * Purpose: Doc PR Merge 확정 시 반영된 content를 새 버전으로 기록
     */
    @Override
    @Transactional
    public void record(Long documentId, String content, Long docPrId) {
        int nextVersionNo = loadDocumentVersionsPort.countByDocumentId(documentId) + 1;

        saveDocumentVersionPort.save(DocumentVersion.builder()
                .documentId(documentId)
                .versionNo(nextVersionNo)
                .content(content)
                .docPrId(docPrId)
                .createdAt(LocalDateTime.now())
                .build());
    }
}
