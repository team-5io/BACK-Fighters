package com.lion._iozoo.document.infrastructure.persistence.entity;

import com.lion._iozoo.document.domain.RelationType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_relations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DocumentRelationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_document_id", nullable = false)
    private Long sourceDocumentId;

    @Column(name = "target_document_id", nullable = false)
    private Long targetDocumentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "relation_type", nullable = false)
    private RelationType relationType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    private DocumentRelationEntity(Long id, Long sourceDocumentId, Long targetDocumentId,
                                    RelationType relationType, LocalDateTime createdAt) {
        this.id = id;
        this.sourceDocumentId = sourceDocumentId;
        this.targetDocumentId = targetDocumentId;
        this.relationType = relationType;
        this.createdAt = createdAt;
    }
}
