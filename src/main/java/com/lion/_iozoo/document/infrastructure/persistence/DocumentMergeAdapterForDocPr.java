package com.lion._iozoo.document.infrastructure.persistence;

import com.lion._iozoo.docpr.application.port.out.MarkDocumentOfficialPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.SaveDocumentPort;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.exception.DocumentNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DocumentMergeAdapterForDocPr implements MarkDocumentOfficialPort {

    private final LoadDocumentPort loadDocumentPort;
    private final SaveDocumentPort saveDocumentPort;

    /**
     * Consumer: docpr
     * Purpose: Doc PR Merge 확정 시 제안된 content를 문서에 반영하고 OFFICIAL로 승격
     */
    @Override
    @Transactional
    public void markOfficial(Long documentId, String content) {
        Document document = loadDocumentPort.loadById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));
        document.markOfficial(content);
        saveDocumentPort.save(document);
    }
}
