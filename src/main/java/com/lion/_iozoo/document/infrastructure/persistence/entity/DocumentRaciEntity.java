package com.lion._iozoo.document.infrastructure.persistence.entity;

import com.lion._iozoo.document.domain.RaciRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_raci")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DocumentRaciEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "raci_role", nullable = false)
    private RaciRole raciRole;

    @Column(name = "assigned_by", nullable = false)
    private Long assignedBy;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @Builder
    private DocumentRaciEntity(Long id, Long documentId, Long userId, RaciRole raciRole,
                               Long assignedBy, LocalDateTime assignedAt) {
        this.id = id;
        this.documentId = documentId;
        this.userId = userId;
        this.raciRole = raciRole;
        this.assignedBy = assignedBy;
        this.assignedAt = assignedAt;
    }
}
