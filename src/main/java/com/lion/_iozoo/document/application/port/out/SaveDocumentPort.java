package com.lion._iozoo.document.application.port.out;

import com.lion._iozoo.document.domain.Document;

public interface SaveDocumentPort {
    Document save(Document document);
}
