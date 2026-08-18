package com.lion._iozoo.document.application.usecase;

import com.lion._iozoo.document.application.command.RequestWritingSuggestionsCommand;
import com.lion._iozoo.document.application.result.WritingSuggestionResult;

import java.util.List;

public interface RequestWritingSuggestionsUseCase {
    List<WritingSuggestionResult> request(Long userId, Long documentId, RequestWritingSuggestionsCommand command);
}
