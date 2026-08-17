package com.lion._iozoo.document.infrastructure.persistence;

import com.lion._iozoo.document.application.port.out.LoadDocumentRelationsPort;
import com.lion._iozoo.document.application.port.out.SaveDocumentRelationPort;
import com.lion._iozoo.document.domain.DocumentRelation;
import com.lion._iozoo.document.infrastructure.persistence.entity.DocumentRelationEntity;
import com.lion._iozoo.document.infrastructure.persistence.mapper.DocumentRelationMapper;
import com.lion._iozoo.document.infrastructure.persistence.repository.DocumentRelationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DocumentRelationPersistenceAdapter implements SaveDocumentRelationPort, LoadDocumentRelationsPort {

    private final DocumentRelationJpaRepository documentRelationJpaRepository;
    private final DocumentRelationMapper documentRelationMapper;

    @Override
    public DocumentRelation save(DocumentRelation documentRelation) {
        DocumentRelationEntity entity = documentRelationMapper.toEntity(documentRelation);
        DocumentRelationEntity saved = documentRelationJpaRepository.save(entity);
        return documentRelationMapper.toDomain(saved);
    }

    @Override
    public List<DocumentRelation> loadByDocumentId(Long documentId) {
        return documentRelationJpaRepository.findBySourceDocumentIdOrTargetDocumentId(documentId, documentId)
                .stream()
                .map(documentRelationMapper::toDomain)
                .toList();
    }
}
