package com.lion._iozoo.document.presentation.api.response;

import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DocumentResponse(
        Long id,
        Long teamId,
        Long authorId,
        String title,
        String content,
        DocumentStatus status,
        boolean restricted
) {
    public static DocumentResponse from(Document document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .teamId(document.getTeamId())
                .authorId(document.getAuthorId())
                .title(document.getTitle())
                .content(document.getContent())
                .status(document.getStatus())
                .restricted(document.isRestricted())
                .build();
    }
}
