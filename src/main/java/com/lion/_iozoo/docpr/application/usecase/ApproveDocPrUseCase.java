package com.lion._iozoo.docpr.application.usecase;

import com.lion._iozoo.docpr.application.command.ApproveDocPrCommand;
import com.lion._iozoo.docpr.domain.DocPr;

public interface ApproveDocPrUseCase {
    DocPr approve(Long userId, ApproveDocPrCommand command);
}
