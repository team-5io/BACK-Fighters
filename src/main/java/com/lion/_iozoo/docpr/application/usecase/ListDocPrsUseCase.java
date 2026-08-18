package com.lion._iozoo.docpr.application.usecase;

import com.lion._iozoo.docpr.domain.DocPr;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListDocPrsUseCase {
    Page<DocPr> list(Long userId, Long teamId, Pageable pageable);
}
