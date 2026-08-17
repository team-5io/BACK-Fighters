package com.lion._iozoo.document.application.port.out;

import com.lion._iozoo.document.domain.DocumentVersion;

public interface SaveDocumentVersionPort {
    DocumentVersion save(DocumentVersion documentVersion);
}
