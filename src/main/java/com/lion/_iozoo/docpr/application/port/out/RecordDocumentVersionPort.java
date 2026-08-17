package com.lion._iozoo.docpr.application.port.out;

public interface RecordDocumentVersionPort {
    void record(Long documentId, String content, Long docPrId);
}
