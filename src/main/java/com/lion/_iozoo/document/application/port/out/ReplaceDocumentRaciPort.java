package com.lion._iozoo.document.application.port.out;

import com.lion._iozoo.document.application.result.DocumentRaciEntry;

import java.util.List;

public interface ReplaceDocumentRaciPort {
    List<DocumentRaciEntry> replaceAll(Long documentId, List<DocumentRaciEntry> newAssignments);
}
