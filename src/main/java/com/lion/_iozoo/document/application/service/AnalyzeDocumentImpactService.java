package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentRaciPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentRelationsPort;
import com.lion._iozoo.document.application.result.DocumentImpactResult;
import com.lion._iozoo.document.application.usecase.AnalyzeDocumentImpactUseCase;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentAccessLevel;
import com.lion._iozoo.document.domain.DocumentRelation;
import com.lion._iozoo.document.domain.exception.DocumentAccessDeniedException;
import com.lion._iozoo.document.domain.exception.DocumentNotFoundException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyzeDocumentImpactService implements AnalyzeDocumentImpactUseCase {

    private final LoadDocumentPort loadDocumentPort;
    private final LoadDocumentRelationsPort loadDocumentRelationsPort;
    private final LoadDocumentRaciPort loadDocumentRaciPort;
    private final TeamPermissionChecker teamPermissionChecker;

    @Override
    @Transactional(readOnly = true)
    public List<DocumentImpactResult> analyze(Long userId, Long documentId) {
        log.info("event=document_impact_analyze_시작 userId={}, documentId={}", userId, documentId);

        try {
            Document anchor = loadDocumentPort.loadById(documentId)
                    .orElseThrow(() -> new DocumentNotFoundException(documentId));

            teamPermissionChecker.requireMember(anchor.getTeamId(), userId);

            if (accessLevelOf(anchor, userId) == DocumentAccessLevel.NONE) {
                throw new DocumentAccessDeniedException(documentId);
            }

            List<DocumentImpactResult> results = breadthFirstSearch(documentId, userId);

            log.info("event=document_impact_analyze_완료 userId={}, documentId={}, count={}",
                    userId, documentId, results.size());
            return results;
        } catch (RuntimeException e) {
            log.warn("event=document_impact_analyze_실패 userId={}, documentId={}, reason={}",
                    userId, documentId, e.getMessage(), e);
            throw e;
        }
    }

    // restricted 문서는 결과에서 숨기고, 그 문서를 통한 하위 탐색도 하지 않는다 (정보 유출 방지).
    private List<DocumentImpactResult> breadthFirstSearch(Long documentId, Long userId) {
        List<DocumentImpactResult> results = new ArrayList<>();
        Set<Long> visited = new HashSet<>();
        visited.add(documentId);

        Deque<Long> queue = new ArrayDeque<>();
        queue.add(documentId);
        int depth = 0;

        while (!queue.isEmpty()) {
            depth++;
            int levelSize = queue.size();

            for (int i = 0; i < levelSize; i++) {
                Long currentId = queue.poll();

                for (Long neighborId : neighborIdsOf(currentId)) {
                    if (visited.contains(neighborId)) {
                        continue;
                    }
                    visited.add(neighborId);

                    Optional<Document> neighbor = loadDocumentPort.loadById(neighborId);
                    if (neighbor.isEmpty()) {
                        continue;
                    }
                    if (accessLevelOf(neighbor.get(), userId) == DocumentAccessLevel.NONE) {
                        continue;
                    }

                    results.add(new DocumentImpactResult(neighborId, neighbor.get().getTitle(), depth));
                    queue.add(neighborId);
                }
            }
        }

        return results;
    }

    private List<Long> neighborIdsOf(Long documentId) {
        return loadDocumentRelationsPort.loadByDocumentId(documentId).stream()
                .map(relation -> otherSide(relation, documentId))
                .toList();
    }

    private Long otherSide(DocumentRelation relation, Long documentId) {
        return relation.getSourceDocumentId().equals(documentId)
                ? relation.getTargetDocumentId()
                : relation.getSourceDocumentId();
    }

    private DocumentAccessLevel accessLevelOf(Document document, Long userId) {
        var role = RaciRoleLookup.roleOf(loadDocumentRaciPort.loadByDocumentId(document.getId()), userId);
        return document.resolveAccessLevel(userId, role);
    }
}
