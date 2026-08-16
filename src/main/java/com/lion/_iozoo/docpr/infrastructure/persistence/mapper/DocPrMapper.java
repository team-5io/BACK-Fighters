package com.lion._iozoo.docpr.infrastructure.persistence.mapper;

import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.infrastructure.persistence.entity.DocPrEntity;
import com.lion._iozoo.global.infrastructure.config.MapStructConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface DocPrMapper {

    DocPrEntity toEntity(DocPr docPr);

    DocPr toDomain(DocPrEntity entity);
}
