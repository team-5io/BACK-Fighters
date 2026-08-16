package com.lion._iozoo.document.presentation.api.response;

import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DocumentResponse(
        @Schema(description = "문서 ID", example = "100")
        Long id,

        @Schema(description = "문서가 속한 팀 ID", example = "1")
        Long teamId,

        @Schema(description = "작성자(R) 사용자 ID", example = "10")
        Long authorId,

        @Schema(description = "문서 제목", example = "온보딩 가이드")
        String title,

        @Schema(description = "문서 내용", example = "본문 내용...")
        String content,

        @Schema(description = "문서 상태", example = "DRAFT")
        DocumentStatus status,

        @Schema(description = "제한 문서 여부 (true면 작성자 본인에게만 노출)", example = "false")
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
