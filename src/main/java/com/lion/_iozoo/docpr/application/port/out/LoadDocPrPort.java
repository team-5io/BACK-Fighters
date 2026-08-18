package com.lion._iozoo.docpr.application.port.out;

import com.lion._iozoo.docpr.domain.DocPr;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface LoadDocPrPort {
    Optional<DocPr> loadById(Long docPrId);
    Page<DocPr> loadByTeamId(Long teamId, Long userId, Pageable pageable);
}
