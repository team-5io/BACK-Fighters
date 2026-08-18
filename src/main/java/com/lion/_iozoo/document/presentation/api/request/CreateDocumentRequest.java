package com.lion._iozoo.document.presentation.api.request;

import com.lion._iozoo.document.domain.Block;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateDocumentRequest(
        @Schema(description = "문서가 속할 팀 ID", example = "1")
        @NotNull(message = "팀 ID는 필수입니다.")
        Long teamId,

        @Schema(description = "문서 제목", example = "온보딩 가이드")
        @NotBlank(message = "제목은 필수입니다.")
        String title,

        @Schema(description = "문서 본문 블록 목록(노션 스타일 에디터). 내용이 없으면 빈 배열 []을 보내야 한다")
        @NotNull(message = "blocks는 필수입니다.")
        List<Block> blocks
) {
}
