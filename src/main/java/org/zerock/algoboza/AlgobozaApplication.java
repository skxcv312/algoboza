package org.zerock.algoboza;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.zerock.algoboza.global.config.DotEnvConfig;

@EnableJpaAuditing
@SpringBootApplication
public class AlgobozaApplication {

    public static void main(String[] args) {
        // 환경 변수 로드
        DotEnvConfig.loadEnv();
        SpringApplication.run(AlgobozaApplication.class, args);
    }

}
