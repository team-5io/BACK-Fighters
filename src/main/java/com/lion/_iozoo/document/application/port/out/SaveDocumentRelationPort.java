package com.lion._iozoo.document.application.port.out;

import com.lion._iozoo.document.domain.DocumentRelation;

public interface SaveDocumentRelationPort {
    DocumentRelation save(DocumentRelation documentRelation);
}
