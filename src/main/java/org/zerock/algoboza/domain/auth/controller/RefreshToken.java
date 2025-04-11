package org.zerock.algoboza.domain.auth.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.algoboza.global.Response;
import org.zerock.algoboza.provider.jwtToken.JwtTokenDTO;
import org.zerock.algoboza.provider.jwtToken.JwtTokenProvider;

@RequiredArgsConstructor
@RestController
public class RefreshToken {
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/refresh-token")
    public ResponseEntity<?> getRefreshToken(@RequestHeader("RefreshToken") String refreshToken) throws JsonProcessingException {

        // 재발급
        JwtTokenDTO newToken = jwtTokenProvider.getTokenWithRefresh(refreshToken);
        HttpHeaders headers = jwtTokenProvider.setTokenToHeader(newToken);

        return Response.builder()
                .status(HttpStatus.OK)
                .massage("Issue a new token")
                .headers(headers)
                .data(newToken)
                .build();

    }
}
