package com.lion._iozoo.docpr.application.usecase;

import com.lion._iozoo.docpr.application.command.MergeDocPrCommand;
import com.lion._iozoo.docpr.domain.DocPr;

public interface MergeDocPrUseCase {
    DocPr merge(Long userId, MergeDocPrCommand command);
}
