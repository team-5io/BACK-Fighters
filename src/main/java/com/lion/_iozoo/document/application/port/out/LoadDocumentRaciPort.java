package com.lion._iozoo.document.application.port.out;

import com.lion._iozoo.document.application.result.DocumentRaciEntry;

import java.util.List;

public interface LoadDocumentRaciPort {
    List<DocumentRaciEntry> loadByDocumentId(Long documentId);
}
