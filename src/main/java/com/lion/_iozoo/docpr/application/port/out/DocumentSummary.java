package com.lion._iozoo.docpr.application.port.out;

// docpr 도메인이 소비하는 document 도메인의 읽기 전용 요약 정보.
public record DocumentSummary(
        Long documentId,
        Long teamId,
        Long authorId,
        boolean draft
) {
}
