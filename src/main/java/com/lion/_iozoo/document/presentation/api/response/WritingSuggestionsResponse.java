package com.lion._iozoo.document.presentation.api.response;

import com.lion._iozoo.document.application.result.WritingSuggestionResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record WritingSuggestionsResponse(
        @Schema(description = "제안 목록")
        List<SuggestionItem> suggestions
) {
    public static WritingSuggestionsResponse from(List<WritingSuggestionResult> results) {
        return WritingSuggestionsResponse.builder()
                .suggestions(results.stream().map(SuggestionItem::from).toList())
                .build();
    }

    public record SuggestionItem(
            @Schema(description = "제안 유형", example = "structure", allowableValues = {"structure", "next-paragraph", "clarity"})
            String type,

            @Schema(description = "제안 내용", example = "string")
            String text
    ) {
        public static SuggestionItem from(WritingSuggestionResult result) {
            return new SuggestionItem(result.type(), result.text());
        }
    }
}
