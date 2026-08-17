package com.lion._iozoo.document.infrastructure.persistence.repository;

import com.lion._iozoo.document.infrastructure.persistence.entity.DocumentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocumentJpaRepository extends JpaRepository<DocumentEntity, Long> {

    // restricted 문서는 작성자 본인에게만 노출한다 (그 외 팀원에게는 목록/검색에서 제외).
    @Query("SELECT d FROM DocumentEntity d WHERE d.teamId = :teamId AND (d.restricted = false OR d.authorId = :userId)")
    Page<DocumentEntity> findByTeamId(@Param("teamId") Long teamId, @Param("userId") Long userId, Pageable pageable);

    // pattern은 어댑터에서 LIKE 와일드카드(%, _)를 이스케이프하고 앞뒤에 %를 붙여 전달한다.
    @Query("SELECT d FROM DocumentEntity d WHERE d.teamId = :teamId AND (d.restricted = false OR d.authorId = :userId) "
            + "AND (d.title LIKE :pattern ESCAPE '\\' OR d.content LIKE :pattern ESCAPE '\\')")
    Page<DocumentEntity> searchByKeyword(@Param("teamId") Long teamId, @Param("userId") Long userId,
                                          @Param("pattern") String pattern, Pageable pageable);
}
