package com.lion._iozoo.docpr.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "doc_pr_status_histories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DocPrStatusHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "doc_pr_id", nullable = false)
    private Long docPrId;

    @Column(name = "from_status", length = 30)
    private String fromStatus;

    @Column(name = "to_status", nullable = false, length = 30)
    private String toStatus;

    @Column(name = "actor_id", nullable = false)
    private Long actorId;

    @Column(length = 500)
    private String reason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    private DocPrStatusHistoryEntity(Long id, Long docPrId, String fromStatus, String toStatus,
                                      Long actorId, String reason, LocalDateTime createdAt) {
        this.id = id;
        this.docPrId = docPrId;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.actorId = actorId;
        this.reason = reason;
        this.createdAt = createdAt;
    }
}
