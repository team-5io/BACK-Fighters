package com.lion._iozoo.document.infrastructure.persistence.entity;

import com.lion._iozoo.document.domain.DocumentStatus;
import com.lion._iozoo.global.infrastructure.persistence.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
