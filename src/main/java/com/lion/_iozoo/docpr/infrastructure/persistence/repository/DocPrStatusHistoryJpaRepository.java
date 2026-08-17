package com.lion._iozoo.docpr.infrastructure.persistence.repository;

import com.lion._iozoo.docpr.infrastructure.persistence.entity.DocPrStatusHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocPrStatusHistoryJpaRepository extends JpaRepository<DocPrStatusHistoryEntity, Long> {
    List<DocPrStatusHistoryEntity> findByDocPrIdOrderByCreatedAtAsc(Long docPrId);
}
