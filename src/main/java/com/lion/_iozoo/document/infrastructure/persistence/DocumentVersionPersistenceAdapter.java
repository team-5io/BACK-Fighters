package com.lion._iozoo.document.infrastructure.persistence;

import com.lion._iozoo.document.application.port.out.LoadDocumentVersionsPort;
import com.lion._iozoo.document.application.port.out.SaveDocumentVersionPort;
import com.lion._iozoo.document.domain.DocumentVersion;
import com.lion._iozoo.document.infrastructure.persistence.entity.DocumentVersionEntity;
import com.lion._iozoo.document.infrastructure.persistence.mapper.DocumentVersionMapper;
import com.lion._iozoo.document.infrastructure.persistence.repository.DocumentVersionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DocumentVersionPersistenceAdapter implements SaveDocumentVersionPort, LoadDocumentVersionsPort {

    private final DocumentVersionJpaRepository documentVersionJpaRepository;
    private final DocumentVersionMapper documentVersionMapper;

    @Override
    public DocumentVersion save(DocumentVersion documentVersion) {
        DocumentVersionEntity entity = documentVersionMapper.toEntity(documentVersion);
        DocumentVersionEntity saved = documentVersionJpaRepository.save(entity);
        return documentVersionMapper.toDomain(saved);
    }

    @Override
    public List<DocumentVersion> loadByDocumentId(Long documentId) {
        return documentVersionJpaRepository.findByDocumentIdOrderByVersionNoAsc(documentId).stream()
                .map(documentVersionMapper::toDomain)
                .toList();
    }

    @Override
    public int countByDocumentId(Long documentId) {
        return documentVersionJpaRepository.countByDocumentId(documentId);
    }
}
