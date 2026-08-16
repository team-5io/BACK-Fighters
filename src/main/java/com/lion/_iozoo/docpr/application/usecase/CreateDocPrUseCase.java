package com.lion._iozoo.docpr.application.usecase;

import com.lion._iozoo.docpr.application.command.CreateDocPrCommand;
import com.lion._iozoo.docpr.domain.DocPr;

public interface CreateDocPrUseCase {
    DocPr create(Long userId, CreateDocPrCommand command);
}
