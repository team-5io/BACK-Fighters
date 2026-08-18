package com.lion._iozoo.global.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AiGatewayConfig {

    // team-5io/AI-Fighters 서버 호출용 클라이언트. base-url이 비어있으면 상대 경로 호출이 되어
    // 항상 연결 실패로 처리되며, 각 도메인은 이를 502로 매핑한다.
    @Bean
    public RestClient aiGatewayRestClient(@Value("${ai-gateway.base-url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
