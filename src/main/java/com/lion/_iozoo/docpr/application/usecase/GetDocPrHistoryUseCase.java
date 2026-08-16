package com.lion._iozoo.docpr.application.usecase;

import com.lion._iozoo.docpr.application.result.DocPrHistoryEntry;

import java.util.List;

public interface GetDocPrHistoryUseCase {
    List<DocPrHistoryEntry> getHistory(Long userId, Long docPrId);
}
