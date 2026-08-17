package com.lion._iozoo.document.application.port.out;

import com.lion._iozoo.document.domain.DocumentRelation;

import java.util.List;

public interface LoadDocumentRelationsPort {
    // documentId가 source 또는 target인 관계를 모두 조회한다 (양방향).
    List<DocumentRelation> loadByDocumentId(Long documentId);
}
