package com.lion._iozoo.docpr.application.port.out;

import com.lion._iozoo.docpr.domain.DocPr;

import java.util.Optional;

public interface LoadDocPrPort {
    Optional<DocPr> loadById(Long docPrId);
}
