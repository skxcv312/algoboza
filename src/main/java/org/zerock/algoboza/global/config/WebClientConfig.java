package org.zerock.algoboza.global.config;

import lombok.extern.log4j.Log4j2;
import org.hibernate.sql.exec.internal.JdbcCallImpl.Builder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Log4j2
public class WebClientConfig {

    @Value("${server-url}")
    private String URL;

    @Value("${fast.api_key}")
    private String apiKey;

    @Value("${proxy.password}")
    private String proxyPassword;
    @Value("${proxy.username}")
    private String proxyUsername;

    @Bean
    public WebClient YoutubeBuilder() {
        return WebClient.builder()
                .baseUrl(URL)
                .defaultHeaders(headers -> {
                    headers.add("api-key", apiKey);
                    headers.add("content-type", "application/json");
                    headers.add("proxy-username", proxyUsername);
                    headers.add("proxy-password", proxyPassword);
                })
                .build();
    }


}
