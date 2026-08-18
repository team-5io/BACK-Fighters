package com.lion._iozoo.document.application.port.out;

import com.lion._iozoo.document.application.result.WritingSuggestionResult;

import java.util.List;

public interface RequestWritingSuggestionsPort {
    // 실패(연결 실패/타임아웃/비2xx) 시 WritingAssistantFailedException을 던진다.
    List<WritingSuggestionResult> requestSuggestions(Long documentId, String content, String cursorContext);
}
