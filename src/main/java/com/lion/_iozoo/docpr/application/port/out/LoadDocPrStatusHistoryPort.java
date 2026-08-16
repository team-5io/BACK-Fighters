package com.lion._iozoo.docpr.application.port.out;

import com.lion._iozoo.docpr.application.result.DocPrHistoryEntry;

import java.util.List;

public interface LoadDocPrStatusHistoryPort {
    List<DocPrHistoryEntry> loadByDocPrId(Long docPrId);
}
