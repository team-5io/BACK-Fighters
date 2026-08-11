package com.lion._iozoo.global.infrastructure.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
        componentModel = "spring", // 스프링 빈으로 자동 등록
        unmappedTargetPolicy = ReportingPolicy.ERROR // 매핑 안 된 필드가 있으면 컴파일 에러 발생
)
public interface MapStructConfig {
}