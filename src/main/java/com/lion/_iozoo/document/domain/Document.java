package com.lion._iozoo.document.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class Document {
    private final Long id;
    private final Long teamId;
    private final Long authorId;
    private String title;
    private String content;
    private List<Block> blocks;
    private DocumentStatus status;
    private boolean restricted;

    @Builder
    private Document(Long id, Long teamId, Long authorId, String title, String content, List<Block> blocks,
                     DocumentStatus status, boolean restricted) {
        this.id = id;
        this.teamId = teamId;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.blocks = blocks == null ? List.of() : blocks;
        this.status = status;
        this.restricted = restricted;
    }

    public boolean isDraft() {
        return this.status == DocumentStatus.DRAFT;
    }

    public void update(String title, List<Block> blocks) {
        this.title = title;
        this.blocks = blocks;
        this.content = flattenText(blocks);
    }

    public void markOfficial(List<Block> blocks) {
        this.blocks = blocks;
        this.content = flattenText(blocks);
        this.status = DocumentStatus.OFFICIAL;
    }

    // 블록 본문을 이어붙여 검색용 평문 캐시를 만든다. 구조(들여쓰기/타입)는 버리고 텍스트만 남긴다.
    public static String flattenText(List<Block> blocks) {
        if (blocks == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        appendBlockText(blocks, sb);
        return sb.toString().strip();
    }

    private static void appendBlockText(List<Block> blocks, StringBuilder sb) {
        for (Block block : blocks) {
            if (block.getContent() != null && !block.getContent().isBlank()) {
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                sb.append(block.getContent());
            }
            appendBlockText(block.getChildren(), sb);
        }
    }

    // RACI 배정이 하나라도 있으면 restricted로 전환하고, 전부 해제되면 원복한다.
    public void applyRaciAssignment(boolean hasAssignments) {
        this.restricted = hasAssignments;
    }

    // role은 이 문서에 대한 요청자의 RACI 역할(없으면 null).
    // restricted가 아니면 팀원 전체 FULL. restricted면 작성자/R/A/C만 FULL,
    // I는 OFFICIAL 문서만 OFFICIAL_ONLY, 역할 없음은 NONE.
    public DocumentAccessLevel resolveAccessLevel(Long userId, RaciRole role) {
        if (!restricted) {
            return DocumentAccessLevel.FULL;
        }
        if (authorId.equals(userId)) {
            return DocumentAccessLevel.FULL;
        }
        if (role == RaciRole.R || role == RaciRole.A || role == RaciRole.C) {
            return DocumentAccessLevel.FULL;
        }
        if (role == RaciRole.I) {
            return status == DocumentStatus.OFFICIAL ? DocumentAccessLevel.OFFICIAL_ONLY : DocumentAccessLevel.NONE;
        }
        return DocumentAccessLevel.NONE;
    }
}
