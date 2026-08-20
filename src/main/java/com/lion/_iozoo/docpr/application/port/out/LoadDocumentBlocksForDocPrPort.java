package com.lion._iozoo.docpr.application.port.out;

import java.util.List;

public interface LoadDocumentBlocksForDocPrPort {
    // 문서 트리를 평탄화해 블록 단위 id+본문 목록으로 반환한다 (본문이 없는 블록은 제외).
    List<DocumentBlockContent> loadFlattenedBlocks(Long documentId);
}
