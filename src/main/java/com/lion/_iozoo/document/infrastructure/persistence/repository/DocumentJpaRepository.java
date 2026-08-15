package com.lion._iozoo.document.infrastructure.persistence.repository;

import com.lion._iozoo.document.infrastructure.persistence.entity.DocumentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocumentJpaRepository extends JpaRepository<DocumentEntity, Long> {

    Page<DocumentEntity> findByTeamId(Long teamId, Pageable pageable);

    @Query("SELECT d FROM DocumentEntity d WHERE d.teamId = :teamId AND (d.title LIKE %:keyword% OR d.content LIKE %:keyword%)")
    Page<DocumentEntity> searchByKeyword(@Param("teamId") Long teamId, @Param("keyword") String keyword, Pageable pageable);
}
