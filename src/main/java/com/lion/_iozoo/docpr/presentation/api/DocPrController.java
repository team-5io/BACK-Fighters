package com.lion._iozoo.docpr.presentation.api;

import com.lion._iozoo.docpr.application.command.AddDocPrReviewCommand;
import com.lion._iozoo.docpr.application.command.ApproveDocPrCommand;
import com.lion._iozoo.docpr.application.command.ChangeDocPrApproverCommand;
import com.lion._iozoo.docpr.application.command.CreateDocPrCommand;
import com.lion._iozoo.docpr.application.command.MergeDocPrCommand;
import com.lion._iozoo.docpr.application.command.RejectDocPrCommand;
import com.lion._iozoo.docpr.application.command.ExceptionMergeDocPrCommand;
import com.lion._iozoo.docpr.application.command.ResubmitDocPrCommand;
import com.lion._iozoo.docpr.application.result.DocPrHistoryEntry;
import com.lion._iozoo.docpr.application.result.DocPrReview;
import com.lion._iozoo.docpr.application.result.MergeCheckResult;
import com.lion._iozoo.docpr.application.usecase.AddDocPrReviewUseCase;
import com.lion._iozoo.docpr.application.usecase.ApproveDocPrUseCase;
import com.lion._iozoo.docpr.application.usecase.ChangeDocPrApproverUseCase;
import com.lion._iozoo.docpr.application.usecase.CreateDocPrUseCase;
import com.lion._iozoo.docpr.application.usecase.ExceptionMergeDocPrUseCase;
import com.lion._iozoo.docpr.application.usecase.GetAiReviewIssuesUseCase;
import com.lion._iozoo.docpr.application.usecase.GetAiReviewUseCase;
import com.lion._iozoo.docpr.application.result.NextAssigneeInfoResult;
import com.lion._iozoo.docpr.application.usecase.GetDocPrHistoryUseCase;
import com.lion._iozoo.docpr.application.usecase.GetDocPrReviewsUseCase;
import com.lion._iozoo.docpr.application.usecase.GetDocPrUseCase;
import com.lion._iozoo.docpr.application.usecase.GetNextAssigneeInfoUseCase;
import com.lion._iozoo.docpr.application.usecase.ListDocPrsUseCase;
import com.lion._iozoo.docpr.application.usecase.MergeCheckDocPrUseCase;
import com.lion._iozoo.docpr.application.usecase.MergeDocPrUseCase;
import com.lion._iozoo.docpr.application.usecase.RejectDocPrUseCase;
import com.lion._iozoo.docpr.application.usecase.RequestAiReviewUseCase;
import com.lion._iozoo.docpr.application.usecase.ResolveAiReviewIssueUseCase;
import com.lion._iozoo.docpr.application.usecase.ResubmitDocPrUseCase;
import com.lion._iozoo.docpr.application.usecase.SkipAiReviewIssueUseCase;
import com.lion._iozoo.docpr.domain.AiReview;
import com.lion._iozoo.docpr.domain.AiReviewIssue;
import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.presentation.api.common.DocPrResponseCode;
import com.lion._iozoo.docpr.presentation.api.request.AddDocPrReviewRequest;
import com.lion._iozoo.docpr.presentation.api.request.ChangeDocPrApproverRequest;
import com.lion._iozoo.docpr.presentation.api.request.CreateDocPrRequest;
import com.lion._iozoo.docpr.presentation.api.request.ExceptionMergeDocPrRequest;
import com.lion._iozoo.docpr.presentation.api.request.RejectDocPrRequest;
import com.lion._iozoo.docpr.presentation.api.request.ResubmitDocPrRequest;
import com.lion._iozoo.docpr.presentation.api.response.AiReviewIssueResponse;
import com.lion._iozoo.docpr.presentation.api.response.AiReviewResponse;
import com.lion._iozoo.docpr.presentation.api.response.DocPrHistoryResponse;
import com.lion._iozoo.docpr.presentation.api.response.DocPrResponse;
import com.lion._iozoo.docpr.presentation.api.response.DocPrReviewResponse;
import com.lion._iozoo.docpr.presentation.api.response.MergeCheckResponse;
import com.lion._iozoo.docpr.presentation.api.response.NextAssigneeInfoResponse;
import com.lion._iozoo.global.presentation.GlobalApiResponse;
import com.lion._iozoo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    private final MergeDocPrUseCase mergeDocPrUseCase;
    private final GetDocPrHistoryUseCase getDocPrHistoryUseCase;
    private final ChangeDocPrApproverUseCase changeDocPrApproverUseCase;
    private final AddDocPrReviewUseCase addDocPrReviewUseCase;
    private final ExceptionMergeDocPrUseCase exceptionMergeDocPrUseCase;
    private final GetDocPrReviewsUseCase getDocPrReviewsUseCase;
    private final GetNextAssigneeInfoUseCase getNextAssigneeInfoUseCase;
    private final RequestAiReviewUseCase requestAiReviewUseCase;
    private final GetAiReviewUseCase getAiReviewUseCase;
    private final ListDocPrsUseCase listDocPrsUseCase;
    private final GetAiReviewIssuesUseCase getAiReviewIssuesUseCase;
    private final ResolveAiReviewIssueUseCase resolveAiReviewIssueUseCase;
    private final SkipAiReviewIssueUseCase skipAiReviewIssueUseCase;

    @Operation(summary = "Doc PR 목록 조회", description = "팀 공간의 Doc PR 목록을 페이지네이션으로 조회한다. 대상 문서에 대한 RACI 접근수준이 FULL(작성자/R/A/C)인 것만 노출된다.")
    @GetMapping("/doc-prs")
    public GlobalApiResponse<Page<DocPrResponse>> listDocPrs(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam Long teamId,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<DocPrResponse> docPrs = listDocPrsUseCase.list(authUser.userId(), teamId, pageable)
                .map(DocPrResponse::from);

        return GlobalApiResponse.ok(DocPrResponseCode.DOC_PRS_FETCHED, docPrs);
    }

    @Operation(summary = "초안 → Doc PR 전환", description = "문서 작성자(R)가 초안을 Doc PR로 전환하고 승인권자(A)를 지정한다.")
    @PostMapping("/documents/{documentId}/doc-prs")
    public GlobalApiResponse<DocPrResponse> createDocPr(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long documentId,
            @RequestBody @Valid CreateDocPrRequest request) {

        CreateDocPrCommand command = new CreateDocPrCommand(
                documentId,
                request.approverMemberId(),
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

    @Operation(summary = "Merge 확정", description = "승인권자(A)가 승인된 Doc PR을 공식 문서로 병합 확정한다.")
    @PostMapping("/doc-prs/{prId}/merge")
    public GlobalApiResponse<DocPrResponse> mergeDocPr(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long prId) {

        MergeDocPrCommand command = new MergeDocPrCommand(prId);

        DocPr docPr = mergeDocPrUseCase.merge(authUser.userId(), command);

        return GlobalApiResponse.ok(DocPrResponseCode.DOC_PR_MERGED, DocPrResponse.from(docPr));
    }

    @Operation(summary = "Doc PR 이력 조회", description = "상태 전이별 수행자·시각·사유 이력을 시간순으로 조회한다.")
    @GetMapping("/doc-prs/{prId}/history")
    public GlobalApiResponse<List<DocPrHistoryResponse>> getDocPrHistory(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long prId) {

        List<DocPrHistoryResponse> history = getDocPrHistoryUseCase.getHistory(authUser.userId(), prId)
                .stream()
                .map(DocPrHistoryResponse::from)
                .toList();

        return GlobalApiResponse.ok(DocPrResponseCode.DOC_PR_HISTORY_FETCHED, history);
    }

    @Operation(summary = "대체 승인권자 지정", description = "팀 관리자가 Doc PR의 승인권자를 교체한다.")
    @PatchMapping("/doc-prs/{prId}/approver")
    public GlobalApiResponse<DocPrResponse> changeDocPrApprover(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long prId,
            @RequestBody @Valid ChangeDocPrApproverRequest request) {

        ChangeDocPrApproverCommand command = new ChangeDocPrApproverCommand(prId, request.newApproverMemberId());

        DocPr docPr = changeDocPrApproverUseCase.changeApprover(authUser.userId(), command);

        return GlobalApiResponse.ok(DocPrResponseCode.DOC_PR_APPROVER_CHANGED, DocPrResponse.from(docPr));
    }

    @Operation(summary = "리뷰어 의견 등록", description = "리뷰어가 Doc PR에 리뷰 의견을 남긴다.")
    @PostMapping("/doc-prs/{prId}/human-reviews")
    public GlobalApiResponse<DocPrReviewResponse> addDocPrReview(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long prId,
            @RequestBody @Valid AddDocPrReviewRequest request) {

        AddDocPrReviewCommand command = new AddDocPrReviewCommand(prId, request.comment());

        DocPrReview review = addDocPrReviewUseCase.addReview(authUser.userId(), command);

        return GlobalApiResponse.created(DocPrResponseCode.DOC_PR_REVIEW_CREATED, DocPrReviewResponse.from(review));
    }

    @Operation(summary = "예외 Merge", description = "승인권자(A)가 차단 조건에도 불구하고 예외적으로 Doc PR을 병합 확정한다.")
    @PostMapping("/doc-prs/{prId}/merge/exception")
    public GlobalApiResponse<DocPrResponse> exceptionMergeDocPr(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long prId,
            @RequestBody @Valid ExceptionMergeDocPrRequest request) {

        ExceptionMergeDocPrCommand command = new ExceptionMergeDocPrCommand(prId, request.reason());

        DocPr docPr = exceptionMergeDocPrUseCase.mergeWithException(authUser.userId(), command);

        return GlobalApiResponse.ok(DocPrResponseCode.DOC_PR_EXCEPTION_MERGED, DocPrResponse.from(docPr));
    }

    @Operation(summary = "리뷰 의견 조회", description = "Doc PR에 등록된 리뷰 의견 목록을 시간순으로 조회한다.")
    @GetMapping("/doc-prs/{prId}/reviews")
    public GlobalApiResponse<List<DocPrReviewResponse>> getDocPrReviews(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long prId) {

        List<DocPrReviewResponse> reviews = getDocPrReviewsUseCase.getReviews(authUser.userId(), prId)
                .stream()
                .map(DocPrReviewResponse::from)
                .toList();

        return GlobalApiResponse.ok(DocPrResponseCode.DOC_PR_REVIEWS_FETCHED, reviews);
    }

    @Operation(summary = "다음 작업자 정보 조회", description = "Follow-the-Sun 워크플로우에서 다음 작업자 지정이 필요한 상태인지와 인수인계 정보 위치(가장 최근 상태 전이 이력)를 조회한다.")
    @GetMapping("/doc-prs/{prId}/next-assignee")
    public GlobalApiResponse<NextAssigneeInfoResponse> getNextAssigneeInfo(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long prId) {

        NextAssigneeInfoResult result = getNextAssigneeInfoUseCase.getInfo(authUser.userId(), prId);

        return GlobalApiResponse.ok(DocPrResponseCode.NEXT_ASSIGNEE_INFO_FETCHED, NextAssigneeInfoResponse.from(result));
    }

    @Operation(summary = "AI 리뷰 요청", description = "DocumentLion에게 문서 충돌·정합성·협업 규칙 위반 여부를 검토받는다. 같은 Doc PR로 재요청하면 이전 결과를 덮어쓴다.")
    @PostMapping("/doc-prs/{prId}/ai-review")
    public GlobalApiResponse<AiReviewResponse> requestAiReview(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long prId) {

        AiReview review = requestAiReviewUseCase.request(authUser.userId(), prId);

        return GlobalApiResponse.ok(DocPrResponseCode.AI_REVIEW_REQUESTED, AiReviewResponse.from(review));
    }

    @Operation(summary = "AI 리뷰 결과 조회", description = "저장된 DocumentLion 검토 결과를 다시 조회한다.")
    @GetMapping("/doc-prs/{prId}/ai-review")
    public GlobalApiResponse<AiReviewResponse> getAiReview(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long prId) {

        AiReview review = getAiReviewUseCase.getByDocPrId(authUser.userId(), prId);

        return GlobalApiResponse.ok(DocPrResponseCode.AI_REVIEW_FETCHED, AiReviewResponse.from(review));
    }

    @Operation(summary = "AI 리뷰 이슈 목록 조회", description = "저장된 AI 리뷰 이슈 중 미해결(UNRESOLVED) 이슈만 조회한다.")
    @GetMapping("/doc-prs/{prId}/ai-review/issues")
    public GlobalApiResponse<List<AiReviewIssueResponse>> getAiReviewIssues(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long prId) {

        List<AiReviewIssueResponse> issues = getAiReviewIssuesUseCase.getUnresolvedIssues(authUser.userId(), prId)
                .stream()
                .map(AiReviewIssueResponse::from)
                .toList();

        return GlobalApiResponse.ok(DocPrResponseCode.AI_REVIEW_ISSUES_FETCHED, issues);
    }

    @Operation(summary = "AI 리뷰 이슈 해결 처리", description = "AI 리뷰 이슈를 해결(RESOLVED) 상태로 변경한다.")
    @PatchMapping("/doc-prs/{prId}/ai-review/issues/{issueId}/resolve")
    public GlobalApiResponse<AiReviewIssueResponse> resolveAiReviewIssue(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long prId,
            @PathVariable Long issueId) {

        AiReviewIssue issue = resolveAiReviewIssueUseCase.resolve(authUser.userId(), prId, issueId);

        return GlobalApiResponse.ok(DocPrResponseCode.AI_REVIEW_ISSUE_RESOLVED, AiReviewIssueResponse.from(issue));
    }

    @Operation(summary = "AI 리뷰 이슈 건너뛰기", description = "AI 리뷰 이슈를 건너뛰기(SKIPPED) 상태로 변경한다.")
    @PatchMapping("/doc-prs/{prId}/ai-review/issues/{issueId}/skip")
    public GlobalApiResponse<AiReviewIssueResponse> skipAiReviewIssue(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long prId,
            @PathVariable Long issueId) {

        AiReviewIssue issue = skipAiReviewIssueUseCase.skip(authUser.userId(), prId, issueId);

        return GlobalApiResponse.ok(DocPrResponseCode.AI_REVIEW_ISSUE_SKIPPED, AiReviewIssueResponse.from(issue));
    }
}
