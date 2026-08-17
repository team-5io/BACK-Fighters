package com.lion._iozoo.document.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_versions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DocumentVersionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @Column(name = "version_no", nullable = false)
    private int versionNo;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @Column(name = "doc_pr_id")
    private Long docPrId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    private DocumentVersionEntity(Long id, Long documentId, int versionNo, String content,
                                   Long docPrId, LocalDateTime createdAt) {
        this.id = id;
        this.documentId = documentId;
        this.versionNo = versionNo;
        this.content = content;
        this.docPrId = docPrId;
        this.createdAt = createdAt;
    }
}
