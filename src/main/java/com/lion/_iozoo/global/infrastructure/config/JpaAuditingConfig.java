package com.lion._iozoo.global.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// @EnableJpaAuditing을 DocPrApplication(SpringBootConfiguration 소스)에 직접 두면
// @WebMvcTest 등 슬라이스 테스트가 이를 걸러내지 못해 jpaMappingContext/EntityManagerFactory가
// 없는 상태에서 jpaAuditingHandler 빈 생성이 실패한다. 별도 설정 클래스로 분리해 슬라이스 테스트에서 제외되게 한다.
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
