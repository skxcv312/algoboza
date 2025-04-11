package org.zerock.algoboza.domain.auth.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.algoboza.global.Response;

@RequiredArgsConstructor
@RestController
public class Logout {

    @GetMapping("/api/logout")
    public Response<?> logout() {
        // 헤더에 정보 삭제
        return Response.builder()
                .status(HttpStatus.OK)
                .message("Successful logout")
                .headers(new HttpHeaders())
                .build();

    }
}
