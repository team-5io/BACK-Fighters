package com.lion._iozoo.docpr.application.port.out;

import com.lion._iozoo.docpr.domain.DocPr;

public interface SaveDocPrPort {
    DocPr save(DocPr docPr);
}
