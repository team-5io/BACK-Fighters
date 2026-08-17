package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.port.out.LoadDocumentRelationsPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.result.DocumentRelationExploreResult;
import com.lion._iozoo.document.application.usecase.GetDocumentRelationsUseCase;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentRelation;
import com.lion._iozoo.document.domain.RelationDirection;
import com.lion._iozoo.document.domain.exception.DocumentNotFoundException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetDocumentRelationsService implements GetDocumentRelationsUseCase {

    private final LoadDocumentPort loadDocumentPort;
    private final LoadDocumentRelationsPort loadDocumentRelationsPort;
    private final TeamPermissionChecker teamPermissionChecker;

    @Override
    @Transactional(readOnly = true)
    public List<DocumentRelationExploreResult> explore(Long userId, Long documentId) {
        log.info("event=document_relation_explore_시작 userId={}, documentId={}", userId, documentId);

        try {
            Document anchor = loadDocumentPort.loadById(documentId)
                    .orElseThrow(() -> new DocumentNotFoundException(documentId));

            teamPermissionChecker.requireMember(anchor.getTeamId(), userId);

            List<DocumentRelation> relations = loadDocumentRelationsPort.loadByDocumentId(documentId);

            List<DocumentRelationExploreResult> results = relations.stream()
                    .flatMap(relation -> toVisibleResult(relation, documentId, userId).stream())
                    .toList();

            log.info("event=document_relation_explore_완료 userId={}, documentId={}, count={}",
                    userId, documentId, results.size());
            return results;
        } catch (RuntimeException e) {
            log.warn("event=document_relation_explore_실패 userId={}, documentId={}, reason={}",
                    userId, documentId, e.getMessage(), e);
            throw e;
        }
    }

    // 이웃 문서가 restricted이고 요청자가 작성자가 아니면 결과에서 숨긴다.
    private Optional<DocumentRelationExploreResult> toVisibleResult(DocumentRelation relation, Long documentId, Long userId) {
        boolean outgoing = relation.getSourceDocumentId().equals(documentId);
        Long neighborId = outgoing ? relation.getTargetDocumentId() : relation.getSourceDocumentId();

        return loadDocumentPort.loadById(neighborId)
                .filter(neighbor -> !neighbor.isRestricted() || neighbor.getAuthorId().equals(userId))
                .map(neighbor -> new DocumentRelationExploreResult(
                        relation.getId(),
                        outgoing ? RelationDirection.OUTGOING : RelationDirection.INCOMING,
                        relation.getRelationType(),
                        neighbor.getId(),
                        neighbor.getTitle(),
                        relation.getCreatedAt()
                ));
    }
}
