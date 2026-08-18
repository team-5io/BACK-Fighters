package com.lion._iozoo.document.presentation.api.common;

import com.lion._iozoo.global.presentation.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DocumentResponseCode implements ResponseCode {
    DOCUMENT_CREATED("DOCUMENT_201_1", "문서가 생성되었습니다."),
    DOCUMENT_UPDATED("DOCUMENT_200_1", "문서가 수정되었습니다."),
    DOCUMENT_DELETED("DOCUMENT_200_2", "문서가 삭제되었습니다."),
    DOCUMENTS_FETCHED("DOCUMENT_200_3", "문서 목록을 조회했습니다."),
    DOCUMENTS_SEARCHED("DOCUMENT_200_4", "문서 검색 결과를 조회했습니다."),
    DOCUMENT_RACI_SET("DOCUMENT_200_5", "RACI 역할이 지정되었습니다."),
    DOCUMENT_RELATION_CREATED("DOCUMENT_201_2", "문서 관계가 생성되었습니다."),
    DOCUMENT_RELATIONS_FETCHED("DOCUMENT_200_6", "문서 관계 그래프를 조회했습니다."),
    DOCUMENT_IMPACT_FETCHED("DOCUMENT_200_7", "Impact Analysis 결과를 조회했습니다."),
    DOCUMENT_VERSIONS_FETCHED("DOCUMENT_200_8", "문서 버전 이력을 조회했습니다."),
    DOCUMENT_MY_PERMISSION_FETCHED("DOCUMENT_200_9", "내 접근 권한을 조회했습니다.");

    private final String code;
    private final String message;
}
