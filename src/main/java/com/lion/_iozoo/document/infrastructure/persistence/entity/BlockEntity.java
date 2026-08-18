package com.lion._iozoo.document.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "blocks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlockEntity {

    @Id
    private String id;

    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @Column(name = "parent_block_id")
    private String parentBlockId;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(nullable = false, length = 20)
    private String type;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Boolean checked;

    private Boolean collapsed;

    @Column(length = 20)
    private String language;

    @Builder
    private BlockEntity(String id, Long documentId, String parentBlockId, int sortOrder, String type,
                        String content, Boolean checked, Boolean collapsed, String language) {
        this.id = id;
        this.documentId = documentId;
        this.parentBlockId = parentBlockId;
        this.sortOrder = sortOrder;
        this.type = type;
        this.content = content;
        this.checked = checked;
        this.collapsed = collapsed;
        this.language = language;
    }
}
