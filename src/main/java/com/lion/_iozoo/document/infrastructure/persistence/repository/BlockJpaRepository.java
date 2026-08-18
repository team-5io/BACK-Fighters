package com.lion._iozoo.document.infrastructure.persistence.repository;

import com.lion._iozoo.document.infrastructure.persistence.entity.BlockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlockJpaRepository extends JpaRepository<BlockEntity, String> {

    List<BlockEntity> findByDocumentIdOrderBySortOrderAsc(Long documentId);

    void deleteByDocumentId(Long documentId);
}
