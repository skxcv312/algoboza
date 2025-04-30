package org.zerock.algoboza.global.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotEnvConfig {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();  // 파일 없으면 무시;

    public static String getEnv(String key) {
        return dotenv.get(key);
    }

    public static void loadEnv() {
        for (DotenvEntry entry : dotenv.entries()) {
            System.setProperty(entry.getKey(), entry.getValue());
        }
    }
}
