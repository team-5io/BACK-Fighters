package com.lion._iozoo.docpr.application.port.out;

public interface CheckDocumentAccessPort {
    boolean hasFullAccess(Long documentId, Long userId);
}
