package org.zerock.arcteryx.domain.auth.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.arcteryx.domain.auth.DTO.UserDTO;
import org.zerock.arcteryx.domain.auth.service.AuthService;
import org.zerock.arcteryx.entity.UserEntity;
import org.zerock.arcteryx.global.Response;
import org.zerock.arcteryx.provider.jwtToken.JwtTokenProvider;

@RequiredArgsConstructor
@RestController
public class Logout {

    @GetMapping("/api/logout")
    public Response<?> logout() {
        // 헤더에 정보 삭제
        return Response.builder()
                .status(HttpStatus.OK)
                .massage("Successful logout")
                .headers(new HttpHeaders())
                .build();

    }
}
