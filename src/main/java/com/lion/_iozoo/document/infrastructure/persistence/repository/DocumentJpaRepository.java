package com.lion._iozoo.document.infrastructure.persistence.repository;

import com.lion._iozoo.document.infrastructure.persistence.entity.DocumentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocumentJpaRepository extends JpaRepository<DocumentEntity, Long> {

    // restricted 문서는 작성자·RACI(R/A/C) 본인에게만 노출하고, I는 OFFICIAL 문서에 한해 노출한다.
    String RACI_VISIBILITY_CONDITION =
            "(d.restricted = false OR d.authorId = :userId "
            + "OR EXISTS (SELECT 1 FROM DocumentRaciEntity r WHERE r.documentId = d.id AND r.userId = :userId AND r.raciRole IN ('R','A','C')) "
            + "OR (d.status = 'OFFICIAL' AND EXISTS (SELECT 1 FROM DocumentRaciEntity r WHERE r.documentId = d.id AND r.userId = :userId AND r.raciRole = 'I')))";

    @Query("SELECT d FROM DocumentEntity d WHERE d.teamId = :teamId AND " + RACI_VISIBILITY_CONDITION)
    Page<DocumentEntity> findByTeamId(@Param("teamId") Long teamId, @Param("userId") Long userId, Pageable pageable);

    // pattern은 어댑터에서 LIKE 와일드카드(%, _)를 이스케이프하고 앞뒤에 %를 붙여 전달한다.
    @Query("SELECT d FROM DocumentEntity d WHERE d.teamId = :teamId AND " + RACI_VISIBILITY_CONDITION
            + " AND (d.title LIKE :pattern ESCAPE '\\' OR d.content LIKE :pattern ESCAPE '\\')")
    Page<DocumentEntity> searchByKeyword(@Param("teamId") Long teamId, @Param("userId") Long userId,
                                          @Param("pattern") String pattern, Pageable pageable);
}
