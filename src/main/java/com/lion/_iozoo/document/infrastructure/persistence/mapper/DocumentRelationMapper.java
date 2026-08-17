package com.lion._iozoo.document.infrastructure.persistence.mapper;

import com.lion._iozoo.document.domain.DocumentRelation;
import com.lion._iozoo.document.infrastructure.persistence.entity.DocumentRelationEntity;
import com.lion._iozoo.global.infrastructure.config.MapStructConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface DocumentRelationMapper {

    DocumentRelationEntity toEntity(DocumentRelation documentRelation);

    DocumentRelation toDomain(DocumentRelationEntity entity);
}
