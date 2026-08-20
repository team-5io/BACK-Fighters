package com.lion._iozoo.docpr.application.port.out;

public record RelatedDocumentContent(
        Long documentId, String title, String content, String relationType, String direction) {
}
