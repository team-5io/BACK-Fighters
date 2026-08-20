package com.lion._iozoo.docpr.application.port.out;

import java.util.List;

public interface LoadRelatedDocumentsForDocPrPort {
    // 문서 관계 조회(GET /documents/relations)와 동일한 RACI 가시성 규칙으로 이웃 문서를 필터링해
    // 본문까지 포함한 목록을 반환한다. AI 리뷰가 conflict/inconsistency를 검토할 때 사용.
    List<RelatedDocumentContent> loadVisibleRelatedDocuments(Long documentId, Long userId);
}
