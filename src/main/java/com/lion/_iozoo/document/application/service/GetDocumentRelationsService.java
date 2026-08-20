package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentRaciPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentRelationsPort;
import com.lion._iozoo.document.application.result.DocumentRelationExploreResult;
import com.lion._iozoo.document.application.result.DocumentWithRelationsResult;
import com.lion._iozoo.document.application.usecase.GetDocumentRelationsUseCase;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentAccessLevel;
import com.lion._iozoo.document.domain.DocumentRelation;
import com.lion._iozoo.document.domain.RelationDirection;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final LoadDocumentRaciPort loadDocumentRaciPort;
    private final TeamPermissionChecker teamPermissionChecker;

    // 팀의 문서 목록(문서 목록 조회와 동일한 RACI 필터링/페이지네이션)을 관계 정보와 함께 조회한다.
    // 관계가 하나도 없는 독립 문서는 relations가 null.
    @Override
    @Transactional(readOnly = true)
    public Page<DocumentWithRelationsResult> explore(Long userId, Long teamId, Pageable pageable) {
        log.info("event=document_relation_explore_시작 userId={}, teamId={}", userId, teamId);

        try {
            teamPermissionChecker.requireMember(teamId, userId);

            Page<Document> documents = loadDocumentPort.loadByTeamId(teamId, userId, pageable);

            Page<DocumentWithRelationsResult> results = documents.map(document -> {
                List<DocumentRelation> relations = loadDocumentRelationsPort.loadByDocumentId(document.getId());
                List<DocumentRelationExploreResult> visible = relations.stream()
                        .flatMap(relation -> toVisibleResult(relation, document.getId(), userId).stream())
                        .toList();
                return new DocumentWithRelationsResult(document, visible.isEmpty() ? null : visible);
            });

            log.info("event=document_relation_explore_완료 userId={}, teamId={}, count={}",
                    userId, teamId, results.getNumberOfElements());
            return results;
        } catch (RuntimeException e) {
            log.warn("event=document_relation_explore_실패 userId={}, teamId={}, reason={}",
                    userId, teamId, e.getMessage(), e);
            throw e;
        }
    }

    // 이웃 문서의 RACI 접근수준이 NONE이면 결과에서 숨긴다.
    private Optional<DocumentRelationExploreResult> toVisibleResult(DocumentRelation relation, Long documentId, Long userId) {
        boolean outgoing = relation.getSourceDocumentId().equals(documentId);
        Long neighborId = outgoing ? relation.getTargetDocumentId() : relation.getSourceDocumentId();

        return loadDocumentPort.loadById(neighborId)
                .filter(neighbor -> accessLevelOf(neighbor, userId) != DocumentAccessLevel.NONE)
                .map(neighbor -> new DocumentRelationExploreResult(
                        relation.getId(),
                        outgoing ? RelationDirection.OUTGOING : RelationDirection.INCOMING,
                        relation.getRelationType(),
                        neighbor.getId(),
                        neighbor.getTitle(),
                        relation.getCreatedAt()
                ));
    }

    private DocumentAccessLevel accessLevelOf(Document document, Long userId) {
        var role = RaciRoleLookup.roleOf(loadDocumentRaciPort.loadByDocumentId(document.getId()), userId);
        return document.resolveAccessLevel(userId, role);
    }
}
