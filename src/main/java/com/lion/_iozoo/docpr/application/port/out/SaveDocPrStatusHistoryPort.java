package com.lion._iozoo.docpr.application.port.out;

import com.lion._iozoo.docpr.domain.DocPrStatus;

public interface SaveDocPrStatusHistoryPort {
    void save(Long docPrId, DocPrStatus fromStatus, DocPrStatus toStatus, Long actorId, String reason);
}
