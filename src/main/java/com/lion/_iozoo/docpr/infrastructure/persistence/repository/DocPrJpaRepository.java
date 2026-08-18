package com.lion._iozoo.docpr.infrastructure.persistence.repository;

import com.lion._iozoo.docpr.infrastructure.persistence.entity.DocPrEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocPrJpaRepository extends JpaRepository<DocPrEntity, Long> {

    // Doc PR 목록은 대상 문서에 대한 RACI 접근수준이 FULL(작성자/R/A/C)인 것만 노출한다.
    // (문서 자체가 restricted가 아니면 팀원 전체에게 FULL이므로 그 경우도 포함)
    @Query("SELECT d FROM DocPrEntity d WHERE EXISTS ("
            + "SELECT 1 FROM DocumentEntity doc WHERE doc.id = d.documentId AND doc.deletedAt IS NULL AND doc.teamId = :teamId AND ("
            + "doc.restricted = false OR doc.authorId = :userId "
            + "OR EXISTS (SELECT 1 FROM DocumentRaciEntity r WHERE r.documentId = doc.id AND r.userId = :userId AND r.raciRole IN ('R','A','C'))"
            + "))")
    Page<DocPrEntity> findAllByTeamId(@Param("teamId") Long teamId, @Param("userId") Long userId, Pageable pageable);
}
