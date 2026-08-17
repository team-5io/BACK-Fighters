package com.lion._iozoo.document.application.usecase;

import com.lion._iozoo.document.application.command.SetDocumentRaciCommand;
import com.lion._iozoo.document.application.result.DocumentRaciEntry;

import java.util.List;

public interface SetDocumentRaciUseCase {
    List<DocumentRaciEntry> setRaci(Long userId, SetDocumentRaciCommand command);
}
