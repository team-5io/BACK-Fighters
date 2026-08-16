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
    DOC_PR_HISTORY_FETCHED("DOCPR_200_7", "Doc PR 이력을 조회했습니다.");

    private final String code;
    private final String message;
}
