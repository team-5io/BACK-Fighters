package com.lion._iozoo.document.infrastructure.persistence;

import com.lion._iozoo.docpr.application.port.out.DocumentBlockContent;
import com.lion._iozoo.docpr.application.port.out.LoadDocumentBlocksForDocPrPort;
import com.lion._iozoo.docpr.application.port.out.LoadRelatedDocumentsForDocPrPort;
import com.lion._iozoo.docpr.application.port.out.RelatedDocumentContent;
import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentRaciPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentRelationsPort;
import com.lion._iozoo.document.application.result.DocumentRaciEntry;
import com.lion._iozoo.document.domain.Block;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentAccessLevel;
import com.lion._iozoo.document.domain.DocumentRelation;
import com.lion._iozoo.document.domain.RaciRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Consumer: docpr
 * Purpose: AI 리뷰(DocumentLion) 요청에 실어 보낼 문서 블록·연결 문서 본문을 조립한다.
 * 연결 문서 필터링은 문서 관계 조회(GET /documents/relations)와 동일한 RACI 가시성 규칙을 따른다.
 */
@Component
@RequiredArgsConstructor
public class DocumentContextAdapterForDocPr implements LoadDocumentBlocksForDocPrPort, LoadRelatedDocumentsForDocPrPort {

    private final LoadDocumentPort loadDocumentPort;
    private final LoadDocumentRelationsPort loadDocumentRelationsPort;
    private final LoadDocumentRaciPort loadDocumentRaciPort;

    @Override
    public List<DocumentBlockContent> loadFlattenedBlocks(Long documentId) {
        return loadDocumentPort.loadById(documentId)
                .map(document -> flatten(document.getBlocks()))
                .orElse(List.of());
    }

    private List<DocumentBlockContent> flatten(List<Block> blocks) {
        List<DocumentBlockContent> result = new ArrayList<>();
        appendFlattened(blocks, result);
        return result;
    }

    private void appendFlattened(List<Block> blocks, List<DocumentBlockContent> acc) {
        for (Block block : blocks) {
            if (block.getContent() != null && !block.getContent().isBlank()) {
                acc.add(new DocumentBlockContent(block.getId(), block.getContent()));
            }
            appendFlattened(block.getChildren(), acc);
        }
    }

    @Override
    public List<RelatedDocumentContent> loadVisibleRelatedDocuments(Long documentId, Long userId) {
        return loadDocumentRelationsPort.loadByDocumentId(documentId).stream()
                .flatMap(relation -> toVisible(relation, documentId, userId).stream())
                .toList();
    }

    private Optional<RelatedDocumentContent> toVisible(DocumentRelation relation, Long documentId, Long userId) {
        boolean outgoing = relation.getSourceDocumentId().equals(documentId);
        Long neighborId = outgoing ? relation.getTargetDocumentId() : relation.getSourceDocumentId();
        String direction = outgoing ? "OUTGOING" : "INCOMING";

        return loadDocumentPort.loadById(neighborId)
                .filter(neighbor -> accessLevelOf(neighbor, userId) != DocumentAccessLevel.NONE)
                .map(neighbor -> new RelatedDocumentContent(
                        neighbor.getId(), neighbor.getTitle(), neighbor.getContent(),
                        relation.getRelationType().name(), direction));
    }

    private DocumentAccessLevel accessLevelOf(Document document, Long userId) {
        RaciRole role = loadDocumentRaciPort.loadByDocumentId(document.getId()).stream()
                .filter(entry -> entry.userId().equals(userId))
                .map(DocumentRaciEntry::role)
                .findFirst()
                .orElse(null);
        return document.resolveAccessLevel(userId, role);
    }
}
