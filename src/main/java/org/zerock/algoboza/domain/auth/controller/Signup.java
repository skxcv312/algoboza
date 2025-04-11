package org.zerock.algoboza.domain.auth.controller;


import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.algoboza.domain.auth.DTO.UserDTO;
import org.zerock.algoboza.global.Response;
import org.zerock.algoboza.provider.jwtToken.JwtTokenDTO;
import org.zerock.algoboza.provider.jwtToken.JwtTokenProvider;
import org.zerock.algoboza.domain.auth.service.AuthService;
import org.zerock.algoboza.domain.mypage.service.ConnectEmailService;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RestController
public class Signup {
    private final Gson gson;
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ConnectEmailService connectEmailService;


    public record signupDTO(
            String email,
            String password,
            String name,
            String birthdate
    ) {
    }


    @PostMapping("/signup")
    public Response<?> signup(@RequestBody signupDTO signupDTO) {

        // 유저 정보 존재
        if (authService.userEmailExist(signupDTO.email())) {
            return Response.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("가입된 유저")
                    .build();
        }

        // user save
        UserDTO user = authService.userSave(signupDTO);

        connectEmailService.addEmails(user.getEmail(), List.of(user.getEmail()));

        // creat token
        JwtTokenDTO jwtTokenDTO = jwtTokenProvider.createToken(user);
        HttpHeaders headers = jwtTokenProvider.setTokenToHeader(jwtTokenDTO);

        return Response.builder()
                .status(HttpStatus.CREATED)
                .message("가입완료")
                .headers(headers)
                .data(jwtTokenDTO)
                .build();

    }
}
