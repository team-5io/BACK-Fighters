package com.lion._iozoo.global.infrastructure.config;

import java.net.http.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class AiGatewayConfig {

    // team-5io/AI-Fighters 서버 호출용 클라이언트. base-url이 비어있으면 상대 경로 호출이 되어
    // 항상 연결 실패로 처리되며, 각 도메인은 이를 502로 매핑한다.
    @Bean
    public RestClient aiGatewayRestClient(@Value("${ai-gateway.base-url}") String baseUrl) {
        // JDK HttpClient 기본값은 HTTP/2 우선 협상을 시도하는데, AI 서버(uvicorn/h11)가 이를
        // 지원하지 않아 body가 유실되고 422(body missing)로 이어진다. HTTP/1.1을 강제한다.
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(new JdkClientHttpRequestFactory(httpClient))
                .build();
    }
}
