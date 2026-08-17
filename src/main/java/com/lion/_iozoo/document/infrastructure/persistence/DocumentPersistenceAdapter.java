package com.lion._iozoo.document.infrastructure.persistence;

import com.lion._iozoo.document.application.port.out.DeleteDocumentPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.SaveDocumentPort;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.exception.DocumentNotFoundException;
import com.lion._iozoo.document.infrastructure.persistence.entity.DocumentEntity;
import com.lion._iozoo.document.infrastructure.persistence.mapper.DocumentMapper;
import com.lion._iozoo.document.infrastructure.persistence.repository.DocumentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
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
    public Page<Document> loadByTeamId(Long teamId, Long userId, Pageable pageable) {
        return documentJpaRepository.findByTeamId(teamId, userId, pageable)
                .map(documentMapper::toDomain);
    }

    @Override
    public Page<Document> searchByKeyword(Long teamId, Long userId, String keyword, Pageable pageable) {
        String pattern = "%" + escapeLikePattern(keyword) + "%";
        return documentJpaRepository.searchByKeyword(teamId, userId, pattern, pageable)
                .map(documentMapper::toDomain);
    }

    @Override
    public void deleteById(Long documentId) {
        try {
            documentJpaRepository.deleteById(documentId);
        } catch (EmptyResultDataAccessException e) {
            throw new DocumentNotFoundException(documentId);
        }
    }

    // LIKE 패턴의 와일드카드(%, _)를 리터럴로 취급하도록 이스케이프한다. 백슬래시부터 먼저 이스케이프해야 한다.
    private String escapeLikePattern(String keyword) {
        return keyword
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
