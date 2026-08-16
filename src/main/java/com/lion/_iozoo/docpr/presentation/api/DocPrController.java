package com.lion._iozoo.docpr.presentation.api;

import com.lion._iozoo.docpr.application.command.ApproveDocPrCommand;
import com.lion._iozoo.docpr.application.command.CreateDocPrCommand;
import com.lion._iozoo.docpr.application.command.RejectDocPrCommand;
import com.lion._iozoo.docpr.application.command.ResubmitDocPrCommand;
import com.lion._iozoo.docpr.application.result.MergeCheckResult;
import com.lion._iozoo.docpr.application.usecase.ApproveDocPrUseCase;
import com.lion._iozoo.docpr.application.usecase.CreateDocPrUseCase;
import com.lion._iozoo.docpr.application.usecase.GetDocPrUseCase;
import com.lion._iozoo.docpr.application.usecase.MergeCheckDocPrUseCase;
import com.lion._iozoo.docpr.application.usecase.RejectDocPrUseCase;
import com.lion._iozoo.docpr.application.usecase.ResubmitDocPrUseCase;
import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.presentation.api.common.DocPrResponseCode;
import com.lion._iozoo.docpr.presentation.api.request.CreateDocPrRequest;
import com.lion._iozoo.docpr.presentation.api.request.RejectDocPrRequest;
import com.lion._iozoo.docpr.presentation.api.request.ResubmitDocPrRequest;
import com.lion._iozoo.docpr.presentation.api.response.DocPrResponse;
import com.lion._iozoo.docpr.presentation.api.response.MergeCheckResponse;
import com.lion._iozoo.global.presentation.GlobalApiResponse;
import com.lion._iozoo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// 클래스 레벨 @RequestMapping 없음: 전환(POST)은 /documents/{documentId}/doc-prs 아래 중첩 리소스지만,
// 생성 이후의 Doc PR 자체 조회·상태전이는 /doc-prs/{prId}로 독립된 리소스라 경로 형태가 서로 다름.
@Tag(name = "Doc PR", description = "Doc PR 워크플로우 API")
@RestController
@RequiredArgsConstructor
public class DocPrController {

    private final CreateDocPrUseCase createDocPrUseCase;
    private final GetDocPrUseCase getDocPrUseCase;
    private final RejectDocPrUseCase rejectDocPrUseCase;
    private final ApproveDocPrUseCase approveDocPrUseCase;
    private final ResubmitDocPrUseCase resubmitDocPrUseCase;
    private final MergeCheckDocPrUseCase mergeCheckDocPrUseCase;

    @Operation(summary = "초안 → Doc PR 전환", description = "문서 작성자(R)가 초안을 Doc PR로 전환하고 승인권자(A)를 지정한다.")
    @PostMapping("/documents/{documentId}/doc-prs")
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

    @Operation(summary = "Doc PR 상세/상태 조회", description = "Doc PR의 현재 상태(생성/AI리뷰/사람리뷰/반려/재제출/확정/리뷰어지정필요)를 조회한다.")
    @GetMapping("/doc-prs/{prId}")
    public GlobalApiResponse<DocPrResponse> getDocPr(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long prId) {

        DocPr docPr = getDocPrUseCase.getById(authUser.userId(), prId);

        return GlobalApiResponse.ok(DocPrResponseCode.DOC_PR_FETCHED, DocPrResponse.from(docPr));
    }

    @Operation(summary = "Doc PR 반려", description = "승인권자(A)가 Doc PR을 반려하고 사유를 기록한다.")
    @PostMapping("/doc-prs/{prId}/reject")
    public GlobalApiResponse<DocPrResponse> rejectDocPr(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long prId,
            @RequestBody @Valid RejectDocPrRequest request) {

        RejectDocPrCommand command = new RejectDocPrCommand(prId, request.reason());

        DocPr docPr = rejectDocPrUseCase.reject(authUser.userId(), command);

        return GlobalApiResponse.ok(DocPrResponseCode.DOC_PR_REJECTED, DocPrResponse.from(docPr));
    }

    @Operation(summary = "Doc PR 승인", description = "승인권자(A)가 Doc PR을 승인 처리한다.")
    @PostMapping("/doc-prs/{prId}/approve")
    public GlobalApiResponse<DocPrResponse> approveDocPr(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long prId) {

        ApproveDocPrCommand command = new ApproveDocPrCommand(prId);

        DocPr docPr = approveDocPrUseCase.approve(authUser.userId(), command);

        return GlobalApiResponse.ok(DocPrResponseCode.DOC_PR_APPROVED, DocPrResponse.from(docPr));
    }

    @Operation(summary = "재제출", description = "요청자(R)가 반려된 Doc PR을 수정 후 재제출한다.")
    @PostMapping("/doc-prs/{prId}/resubmit")
    public GlobalApiResponse<DocPrResponse> resubmitDocPr(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long prId,
            @RequestBody @Valid ResubmitDocPrRequest request) {

        ResubmitDocPrCommand command = new ResubmitDocPrCommand(prId, request.proposedContent());

        DocPr docPr = resubmitDocPrUseCase.resubmit(authUser.userId(), command);

        return GlobalApiResponse.ok(DocPrResponseCode.DOC_PR_RESUBMITTED, DocPrResponse.from(docPr));
    }

    @Operation(summary = "Merge 가능 여부 확인", description = "승인권자(A)가 Doc PR을 병합해도 되는지 미리 확인한다.")
    @GetMapping("/doc-prs/{prId}/merge-check")
    public GlobalApiResponse<MergeCheckResponse> checkMergeable(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long prId) {

        MergeCheckResult result = mergeCheckDocPrUseCase.checkMergeable(authUser.userId(), prId);

        return GlobalApiResponse.ok(DocPrResponseCode.DOC_PR_MERGE_CHECKED, MergeCheckResponse.from(result));
    }
}
