package com.lion._iozoo.document.infrastructure.persistence;

import com.lion._iozoo.document.application.port.out.LoadDocumentRaciPort;
import com.lion._iozoo.document.application.port.out.ReplaceDocumentRaciPort;
import com.lion._iozoo.document.application.result.DocumentRaciEntry;
import com.lion._iozoo.document.infrastructure.persistence.entity.DocumentRaciEntity;
import com.lion._iozoo.document.infrastructure.persistence.repository.DocumentRaciJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DocumentRaciPersistenceAdapter implements LoadDocumentRaciPort, ReplaceDocumentRaciPort {

    private final DocumentRaciJpaRepository documentRaciJpaRepository;

    @Override
    public List<DocumentRaciEntry> loadByDocumentId(Long documentId) {
        return documentRaciJpaRepository.findByDocumentId(documentId).stream()
                .map(entity -> new DocumentRaciEntry(
                        entity.getUserId(), entity.getRaciRole(), entity.getAssignedBy(), entity.getAssignedAt()
                ))
                .toList();
    }

    @Override
    @Transactional
    public List<DocumentRaciEntry> replaceAll(Long documentId, List<DocumentRaciEntry> newAssignments) {
        documentRaciJpaRepository.deleteByDocumentId(documentId);
        // 삭제를 먼저 flush하지 않으면, 같은 트랜잭션 내에서 동일 (document_id, user_id) 조합으로
        // 재삽입할 때 아직 반영되지 않은 기존 행과 유니크 제약이 충돌한다.
        documentRaciJpaRepository.flush();

        List<DocumentRaciEntity> saved = documentRaciJpaRepository.saveAll(
                newAssignments.stream()
                        .map(entry -> DocumentRaciEntity.builder()
                                .documentId(documentId)
                                .userId(entry.userId())
                                .raciRole(entry.role())
                                .assignedBy(entry.assignedBy())
                                .assignedAt(entry.assignedAt())
                                .build())
                        .toList()
        );

        return saved.stream()
                .map(entity -> new DocumentRaciEntry(
                        entity.getUserId(), entity.getRaciRole(), entity.getAssignedBy(), entity.getAssignedAt()
                ))
                .toList();
    }
}
