package org.zerock.arcteryx.provider.jwtToken;

import com.google.gson.Gson;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import org.zerock.arcteryx.config.JwtConfig;
import org.zerock.arcteryx.entity.UserEntity;

import org.zerock.arcteryx.repository.UserRepo;

import javax.crypto.SecretKey;
import java.util.Date;



@Slf4j
@Configuration
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final UserRepo userRepository;
    private final Gson gson;
    private final JwtConfig SECRET_KEY;

    private final long  accessTokenValidTime = 60 * 60 * 1000L; // 1h
    private final long  refreshTokenValidTime = 24 * 60 * 60 * 1000L; //1 day


    private String setAccessToken(UserEntity user){
        Date now = new Date();
        String userJson = gson.toJson(user);
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userInfo", userJson) // 정보 저장
                .issuedAt(now) // 토큰 발행 시간 정보
                .expiration(new Date(now.getTime() + accessTokenValidTime)) // 토큰 유효시각 설정
                .signWith(SECRET_KEY.getSecretKey())  // 암호화 알고리즘과, secret 값
                .compact();
    }


    private String setRefreshToken(UserEntity user){
        Date now = new Date();
        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshTokenValidTime))
                .signWith(SECRET_KEY.getSecretKey())
                .compact();
    }

    // 토큰 생성
    public JwtTokenDTO createToken(UserEntity user) {  // userPK = email

        String accessToken = setAccessToken(user);
        String refreshToken = setRefreshToken(user);
        return new JwtTokenDTO(accessToken,refreshToken);
    }

    // 리프레시토큰으로 엑세스토큰얻기
    public String getAccessTokenWithRefresh(String refreshToken) {
        String token = refreshToken.replace("Bearer ", "");
        String userEmail = getUserEmail(token);

        if (userEmail == null) {return null;}

        UserEntity user = userRepository.findByEmail(userEmail);
        if (user == null) {return null;}

        return setAccessToken(user);
    }

    // 인증 정보 조회
    public UserEntity getAuthentication(String accessToken) {
        String token = accessToken.replace("Bearer ", "");

        String userEmail = getUserEmail(token);

        if(userEmail == null) { return null;}

        return userRepository.findByEmail(userEmail);

    }

    // 토큰에서 회원 정보 추출
    public String getUserEmail(String token) {
        try {
            Claims claims = validateToken(token);
            return claims.getSubject();

        }catch (ExpiredJwtException expiredJwtException){
            throw expiredJwtException;
        }
        catch (JwtException jwtException){
            throw jwtException;
        }

    }

    // 토큰 유효성, 만료일자 확인
    public Claims validateToken(String token)  {
        try {
           return Jwts.parser()
                    .verifyWith((SecretKey) SECRET_KEY.getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException expiredJwtException) {
            log.info("JWT 만료됨 ", expiredJwtException);
            throw expiredJwtException;
        } catch (JwtException jwtException) {
            log.info("JWT 오류 ", jwtException );
            throw jwtException;
        }
    }

    public ResponseCookie setTokenToCookie(String tokenKey, String tokenValue, long maxAge) {
        return ResponseCookie.from(tokenKey, tokenValue)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(maxAge)
                .sameSite("Strict")
                .build();
    }

    public HttpHeaders setTokenToHeader(JwtTokenDTO token) {
        String accessToken = token.getAccessToken();
        String refreshToken = token.getRefreshToken();


        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken); // Authorization 헤더에 토큰 추가
        responseHeaders.set("RefreshToken", refreshToken);

        log.info("resHeaders : " +responseHeaders.toString());

        return responseHeaders;
    }

}

