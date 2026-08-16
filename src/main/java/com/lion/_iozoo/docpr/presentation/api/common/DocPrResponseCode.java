package com.lion._iozoo.docpr.presentation.api.common;

import com.lion._iozoo.global.presentation.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DocPrResponseCode implements ResponseCode {
    DOC_PR_CREATED("DOCPR_201_1", "Doc PR이 생성되었습니다.");

    private final String code;
    private final String message;
}
