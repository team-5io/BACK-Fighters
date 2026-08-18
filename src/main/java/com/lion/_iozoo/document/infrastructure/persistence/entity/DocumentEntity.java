package com.lion._iozoo.document.infrastructure.persistence.entity;

import com.lion._iozoo.document.domain.DocumentStatus;
import com.lion._iozoo.global.infrastructure.persistence.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DocumentEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status;

    @Column(name = "is_restricted", nullable = false)
    private boolean restricted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    private DocumentEntity(Long id, Long teamId, Long authorId, String title, String content,
                           DocumentStatus status, boolean restricted) {
        this.id = id;
        this.teamId = teamId;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.status = status;
        this.restricted = restricted;
    }

    // 하드 삭제 대신 soft delete만 한다 — doc_prs/document_versions/document_raci/translations 등
    // documents를 참조하는 이력 테이블이 많아, 실제로 지우면 FK 위반 또는 이력 손실이 발생한다.
    public void softDelete(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
