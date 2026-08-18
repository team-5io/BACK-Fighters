package com.lion._iozoo.document.presentation.api.request;

import com.lion._iozoo.document.domain.Block;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UpdateDocumentRequest(
        @Schema(description = "문서 제목", example = "온보딩 가이드 (개정)")
        @NotBlank(message = "제목은 필수입니다.")
        String title,

        @Schema(description = "문서 본문 블록 목록(노션 스타일 에디터)")
        List<Block> blocks
) {
}
