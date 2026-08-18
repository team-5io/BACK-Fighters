package com.lion._iozoo.document.presentation.api.response;

import com.lion._iozoo.document.application.port.out.UserSummary;
import com.lion._iozoo.document.domain.Block;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record DocumentResponse(
        @Schema(description = "문서 ID", example = "100")
        Long id,

        @Schema(description = "문서가 속한 팀 ID", example = "1")
        Long teamId,

        @Schema(description = "담당자 (작성자/R)")
        DocumentAssigneeResponse assignee,

        @Schema(description = "문서 제목", example = "온보딩 가이드")
        String title,

        @Schema(description = "블록 텍스트를 이어붙인 평문 미리보기(검색·목록용). 원본 구조는 blocks 참고")
        String content,

        @Schema(description = "문서 본문 블록 목록. 목록/검색 결과에서는 비어있고, 단건 조회·생성·수정 응답에만 채워진다")
        List<Block> blocks,

        @Schema(description = "문서 상태", example = "DRAFT")
        DocumentStatus status,

        @Schema(description = "제한 문서 여부 (true면 작성자 본인에게만 노출)", example = "false")
        boolean restricted
) {
    public static DocumentResponse from(Document document, UserSummary authorSummary) {
        return DocumentResponse.builder()
                .id(document.getId())
                .teamId(document.getTeamId())
                .assignee(DocumentAssigneeResponse.of(document.getAuthorId(), authorSummary))
                .title(document.getTitle())
                .content(document.getContent())
                .blocks(document.getBlocks())
                .status(document.getStatus())
                .restricted(document.isRestricted())
                .build();
    }
}
