package com.lion._iozoo.document.application.result;

import java.util.List;

public record TranslationGatewayResult(
        String translatedContent,
        List<String> preservedTerms
) {
}
