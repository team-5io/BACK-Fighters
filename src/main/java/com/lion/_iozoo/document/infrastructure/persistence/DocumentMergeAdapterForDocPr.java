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
     * Purpose: Doc PR Merge 확정 시 문서를 OFFICIAL로 승격한다. 문서는 작성자가 계속 편집해온
     * 자기 블록을 이미 갖고 있으므로, 그 블록을 그대로 승격한다 (DocPr.proposedContent는
     * 평문 변경 설명일 뿐이라 병합 콘텐츠로 재파싱하지 않는다).
     */
    @Override
    @Transactional
    public void markOfficial(Long documentId) {
        Document document = loadDocumentPort.loadById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));
        document.markOfficial(document.getBlocks());
        saveDocumentPort.save(document);
    }
}
