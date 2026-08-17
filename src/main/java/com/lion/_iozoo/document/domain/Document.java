package com.lion._iozoo.document.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Document {
    private final Long id;
    private final Long teamId;
    private final Long authorId;
    private String title;
    private String content;
    private DocumentStatus status;
    private final boolean restricted;

    @Builder
    private Document(Long id, Long teamId, Long authorId, String title, String content,
                     DocumentStatus status, boolean restricted) {
        this.id = id;
        this.teamId = teamId;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.status = status;
        this.restricted = restricted;
    }

    public boolean isDraft() {
        return this.status == DocumentStatus.DRAFT;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void markOfficial(String content) {
        this.content = content;
        this.status = DocumentStatus.OFFICIAL;
    }
}
