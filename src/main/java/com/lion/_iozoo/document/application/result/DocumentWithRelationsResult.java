package com.lion._iozoo.document.application.result;

import com.lion._iozoo.document.domain.Document;

import java.util.List;

// relations는 그 문서와 연결된 관계가 하나도 없는 독립 문서면 null이다 (빈 배열이 아님).
public record DocumentWithRelationsResult(
        Document document,
        List<DocumentRelationExploreResult> relations
) {
}
