package com.lion._iozoo.docpr.application.port.out;

import java.util.Optional;

public interface LoadDocumentForDocPrPort {
    Optional<DocumentSummary> loadSummary(Long documentId);
}
