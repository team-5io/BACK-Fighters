package com.lion._iozoo.document.infrastructure.persistence;

import com.lion._iozoo.document.application.port.out.DeleteDocumentPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.SaveDocumentPort;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.infrastructure.persistence.entity.DocumentEntity;
import com.lion._iozoo.document.infrastructure.persistence.mapper.DocumentMapper;
import com.lion._iozoo.document.infrastructure.persistence.repository.DocumentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DocumentPersistenceAdapter implements SaveDocumentPort, LoadDocumentPort, DeleteDocumentPort {

    private final DocumentJpaRepository documentJpaRepository;
    private final DocumentMapper documentMapper;

    @Override
    public Document save(Document document) {
        DocumentEntity entity = documentMapper.toEntity(document);
        DocumentEntity savedEntity = documentJpaRepository.save(entity);
        return documentMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Document> loadById(Long documentId) {
        return documentJpaRepository.findById(documentId)
                .map(documentMapper::toDomain);
    }

    @Override
    public Page<Document> loadByTeamId(Long teamId, Pageable pageable) {
        return documentJpaRepository.findByTeamId(teamId, pageable)
                .map(documentMapper::toDomain);
    }

    @Override
    public Page<Document> searchByKeyword(Long teamId, String keyword, Pageable pageable) {
        return documentJpaRepository.searchByKeyword(teamId, keyword, pageable)
                .map(documentMapper::toDomain);
    }

    @Override
    public void deleteById(Long documentId) {
        documentJpaRepository.deleteById(documentId);
    }
}
