package com.lion._iozoo.document.application.port.out;

import com.lion._iozoo.document.domain.DocumentVersion;

import java.util.List;

public interface LoadDocumentVersionsPort {
    // 버전 번호(versionNo) 오름차순으로 조회한다.
    List<DocumentVersion> loadByDocumentId(Long documentId);

    // 다음 버전 번호 계산용 (버전 번호는 삭제 없이 순차 증가하므로 count == 마지막 버전 번호).
    int countByDocumentId(Long documentId);
}
