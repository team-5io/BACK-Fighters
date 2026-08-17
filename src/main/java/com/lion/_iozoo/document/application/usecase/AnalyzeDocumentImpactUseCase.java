package com.lion._iozoo.document.application.usecase;

import com.lion._iozoo.document.application.result.DocumentImpactResult;

import java.util.List;

public interface AnalyzeDocumentImpactUseCase {
    List<DocumentImpactResult> analyze(Long userId, Long documentId);
}
