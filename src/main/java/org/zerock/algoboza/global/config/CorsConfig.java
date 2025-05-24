package org.zerock.algoboza.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 정확한 origin 명시 (credentials=true일 때 "*" 사용 금지!
        config.setAllowedOrigins(
                List.of("http://192.168.219.174:5173",
                        "http://localhost:5173",
                        "chrome-extension://lflppaeeeiihpeppipkfbgbohebnlpcc",// 확장자,
                        "chrome-extension://ofplmlpjpdnpgiaphkopapnmcicnfapf"

                ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));

        // 자격 증명 허용 (쿠키, Authorization 헤더 등
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
