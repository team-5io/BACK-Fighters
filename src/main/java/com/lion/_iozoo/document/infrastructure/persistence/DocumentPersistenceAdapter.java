package com.lion._iozoo.document.infrastructure.persistence;

import com.lion._iozoo.document.application.port.out.DeleteDocumentPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.SaveDocumentPort;
import com.lion._iozoo.document.domain.Block;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.exception.DocumentNotFoundException;
import com.lion._iozoo.document.infrastructure.persistence.entity.BlockEntity;
import com.lion._iozoo.document.infrastructure.persistence.entity.DocumentEntity;
import com.lion._iozoo.document.infrastructure.persistence.mapper.DocumentMapper;
import com.lion._iozoo.document.infrastructure.persistence.repository.BlockJpaRepository;
import com.lion._iozoo.document.infrastructure.persistence.repository.DocumentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DocumentPersistenceAdapter implements SaveDocumentPort, LoadDocumentPort, DeleteDocumentPort {

    private final DocumentJpaRepository documentJpaRepository;
    private final BlockJpaRepository blockJpaRepository;
    private final DocumentMapper documentMapper;

    @Override
    @Transactional
    public Document save(Document document) {
        DocumentEntity entity = documentMapper.toEntity(document);
        DocumentEntity savedEntity = documentJpaRepository.save(entity);

        blockJpaRepository.deleteByDocumentId(savedEntity.getId());
        List<BlockEntity> blockEntities = new ArrayList<>();
        flattenBlocks(savedEntity.getId(), null, document.getBlocks(), blockEntities);
        blockJpaRepository.saveAll(blockEntities);

        return Document.builder()
                .id(savedEntity.getId())
                .teamId(savedEntity.getTeamId())
                .authorId(savedEntity.getAuthorId())
                .title(savedEntity.getTitle())
                .content(savedEntity.getContent())
                .blocks(document.getBlocks())
                .status(savedEntity.getStatus())
                .restricted(savedEntity.isRestricted())
                .build();
    }

    @Override
    public Optional<Document> loadById(Long documentId) {
        return documentJpaRepository.findById(documentId)
                .map(entity -> Document.builder()
                        .id(entity.getId())
                        .teamId(entity.getTeamId())
                        .authorId(entity.getAuthorId())
                        .title(entity.getTitle())
                        .content(entity.getContent())
                        .blocks(loadBlocks(documentId))
                        .status(entity.getStatus())
                        .restricted(entity.isRestricted())
                        .build());
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

    private void flattenBlocks(Long documentId, String parentBlockId, List<Block> blocks, List<BlockEntity> out) {
        int order = 0;
        for (Block block : blocks) {
            String id = block.getId() != null ? block.getId() : UUID.randomUUID().toString();
            out.add(BlockEntity.builder()
                    .id(id)
                    .documentId(documentId)
                    .parentBlockId(parentBlockId)
                    .sortOrder(order++)
                    .type(block.getType())
                    .content(block.getContent())
                    .checked(block.getChecked())
                    .collapsed(block.getCollapsed())
                    .language(block.getLanguage())
                    .build());
            flattenBlocks(documentId, id, block.getChildren(), out);
        }
    }

    private List<Block> loadBlocks(Long documentId) {
        List<BlockEntity> entities = blockJpaRepository.findByDocumentIdOrderBySortOrderAsc(documentId);

        Map<String, List<BlockEntity>> childrenByParent = new HashMap<>();
        List<BlockEntity> roots = new ArrayList<>();
        for (BlockEntity entity : entities) {
            if (entity.getParentBlockId() == null) {
                roots.add(entity);
            } else {
                childrenByParent.computeIfAbsent(entity.getParentBlockId(), key -> new ArrayList<>()).add(entity);
            }
        }
        return buildBlocks(roots, childrenByParent);
    }

    private List<Block> buildBlocks(List<BlockEntity> entities, Map<String, List<BlockEntity>> childrenByParent) {
        List<Block> blocks = new ArrayList<>();
        for (BlockEntity entity : entities) {
            List<BlockEntity> childEntities = childrenByParent.getOrDefault(entity.getId(), List.of());
            blocks.add(Block.builder()
                    .id(entity.getId())
                    .type(entity.getType())
                    .content(entity.getContent())
                    .checked(entity.getChecked())
                    .collapsed(entity.getCollapsed())
                    .language(entity.getLanguage())
                    .children(buildBlocks(childEntities, childrenByParent))
                    .build());
        }
        return blocks;
    }
}
