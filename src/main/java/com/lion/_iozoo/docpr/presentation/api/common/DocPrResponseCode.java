package com.lion._iozoo.docpr.presentation.api.common;

import com.lion._iozoo.global.presentation.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DocPrResponseCode implements ResponseCode {
    DOC_PR_CREATED("DOCPR_201_1", "Doc PR이 생성되었습니다."),
    DOC_PR_FETCHED("DOCPR_200_1", "Doc PR을 조회했습니다."),
    DOC_PR_REJECTED("DOCPR_200_2", "Doc PR이 반려되었습니다."),
    DOC_PR_APPROVED("DOCPR_200_3", "Doc PR이 승인되었습니다."),
    DOC_PR_RESUBMITTED("DOCPR_200_4", "Doc PR이 재제출되었습니다."),
    DOC_PR_MERGE_CHECKED("DOCPR_200_5", "Merge 가능 여부를 확인했습니다."),
    DOC_PR_MERGED("DOCPR_200_6", "Doc PR이 병합 확정되었습니다."),
    DOC_PR_HISTORY_FETCHED("DOCPR_200_7", "Doc PR 이력을 조회했습니다."),
    DOC_PR_APPROVER_CHANGED("DOCPR_200_8", "승인권자가 변경되었습니다."),
    DOC_PR_REVIEW_CREATED("DOCPR_201_2", "리뷰 의견이 등록되었습니다."),
    DOC_PR_EXCEPTION_MERGED("DOCPR_200_9", "Doc PR이 예외적으로 병합 확정되었습니다."),
    DOC_PR_REVIEWS_FETCHED("DOCPR_200_10", "리뷰 의견 목록을 조회했습니다."),
    NEXT_ASSIGNEE_INFO_FETCHED("DOCPR_200_11", "다음 작업자 정보를 조회했습니다."),
    AI_REVIEW_REQUESTED("DOCPR_200_12", "AI 리뷰를 요청했습니다."),
    AI_REVIEW_FETCHED("DOCPR_200_13", "AI 리뷰 결과를 조회했습니다."),
    DOC_PRS_FETCHED("DOCPR_200_14", "Doc PR 목록을 조회했습니다."),
    AI_REVIEW_ISSUES_FETCHED("DOCPR_200_15", "AI 리뷰 이슈 목록을 조회했습니다."),
    AI_REVIEW_ISSUE_RESOLVED("DOCPR_200_16", "AI 리뷰 이슈를 해결 처리했습니다."),
    AI_REVIEW_ISSUE_SKIPPED("DOCPR_200_17", "AI 리뷰 이슈를 건너뛰기 처리했습니다.");

    private final String code;
    private final String message;
}
