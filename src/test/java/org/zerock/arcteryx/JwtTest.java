package org.zerock.arcteryx;

import io.jsonwebtoken.Claims;
import org.assertj.core.api.Assertions;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zerock.arcteryx.config.JwtConfig;
import org.zerock.arcteryx.entity.UserEntity;
import org.zerock.arcteryx.provider.jwtToken.JwtTokenDTO;
import org.zerock.arcteryx.provider.jwtToken.JwtTokenProvider;
import org.zerock.arcteryx.repository.UserRepo;

import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JwtTest {
    private static final Logger log = LoggerFactory.getLogger(JwtTest.class);
        private UserRepo userRepository;
        private Gson gson;
        private JwtTokenProvider tokenProvider;
        private UserEntity testUser;
        private JwtConfig jwtConfig;




    record User(Long id,
                String name,
                String email,
                String password,
                LocalDate birthDate,
                Instant createdAt
    ) { }

    @BeforeEach
    void setup(){
        gson = new Gson();
        jwtConfig = new JwtConfig("secresdfjlwndmlvnsdlfnwlfndfhwnflksjdbffnglsngwlnegsfbglsnfknt");
        tokenProvider = new JwtTokenProvider(userRepository,gson,jwtConfig);
        testUser = UserEntity.builder()
                .birthDate("2001-09-06")
                .email("test@test.com")
                .name("Test")
                .password("password")
                .build();


    }

    @Test
    void 키생성(){
        String key = jwtConfig.getSecretKey().toString();
        Assertions.assertThat(key).isNotNull();
        log.info("key: {}", key);
    }


    @Test
    void 전체토큰만들기(){
        JwtTokenDTO tokenDTO = tokenProvider.createToken(testUser);
        log.info(gson.toJson(tokenDTO));
        assertNotNull(tokenDTO, "JWT 생성 실패");
    }

    @Test
    void 리프레시토큰으로_엑세스토큰얻기(){
        JwtTokenDTO tokenDTO = tokenProvider.createToken(testUser);
        assertNotNull(tokenDTO, "JWT 생성 실패");

        String reToken = tokenProvider.getAccessTokenWithRefresh(tokenDTO.getRefreshToken());




        Assertions.assertThat(reToken)
                        .isEqualTo(tokenDTO
                        .getRefreshToken());

        log.info(gson.toJson(tokenDTO));
        log.info(reToken);

    }

    @Test
    void 인증정보조회(){
        JwtTokenDTO tokenDTO = tokenProvider.createToken(testUser);
        assertNotNull(tokenDTO, "JWT 생성 실패");

        UserEntity user = tokenProvider.getAuthentication(tokenDTO.getAccessToken());
        log.info(gson.toJson(user));

        Assertions.assertThat(user).isEqualTo(testUser);

    }

    @Test
    void 토큰에서_회원정보_추출(){
        JwtTokenDTO tokenDTO = tokenProvider.createToken(testUser);
        assertNotNull(tokenDTO, "JWT 생성 실패");

        String userEmail = tokenProvider.getUserEmail(tokenDTO.getAccessToken());
        log.info(gson.toJson(tokenDTO));

        Assertions.assertThat(userEmail).isEqualTo(testUser.getEmail());

    }


    @Test

    void 토큰유효성_검증() throws InterruptedException {

        JwtTokenDTO tokenDTO = tokenProvider.createToken(testUser);
        assertNotNull(tokenDTO, "JWT 생성 실패");

        Thread.sleep(3000);

        Claims claims = tokenProvider.validateToken(tokenDTO.getAccessToken());
        Assertions.assertThat(claims)
                .isNull();
    }

}
