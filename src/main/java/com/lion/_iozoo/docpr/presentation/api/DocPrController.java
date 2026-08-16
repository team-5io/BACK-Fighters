package com.lion._iozoo.docpr.presentation.api;

import com.lion._iozoo.docpr.application.command.CreateDocPrCommand;
import com.lion._iozoo.docpr.application.usecase.CreateDocPrUseCase;
import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.presentation.api.common.DocPrResponseCode;
import com.lion._iozoo.docpr.presentation.api.request.CreateDocPrRequest;
import com.lion._iozoo.docpr.presentation.api.response.DocPrResponse;
import com.lion._iozoo.global.presentation.GlobalApiResponse;
import com.lion._iozoo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Doc PR", description = "Doc PR 워크플로우 API")
@RestController
@RequestMapping("/documents/{documentId}/doc-prs")
@RequiredArgsConstructor
public class DocPrController {

    private final CreateDocPrUseCase createDocPrUseCase;

    @Operation(summary = "초안 → Doc PR 전환", description = "문서 작성자(R)가 초안을 Doc PR로 전환하고 승인권자(A)를 지정한다.")
    @PostMapping
    public GlobalApiResponse<DocPrResponse> createDocPr(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long documentId,
            @RequestBody @Valid CreateDocPrRequest request) {

        CreateDocPrCommand command = new CreateDocPrCommand(
                documentId,
                request.approverId(),
                request.proposedContent()
        );

        DocPr docPr = createDocPrUseCase.create(authUser.userId(), command);

        return GlobalApiResponse.created(DocPrResponseCode.DOC_PR_CREATED, DocPrResponse.from(docPr));
    }
}
