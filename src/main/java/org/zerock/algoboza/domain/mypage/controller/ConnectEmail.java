package org.zerock.algoboza.domain.mypage.controller;


import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.algoboza.domain.auth.service.AuthService;
import org.zerock.algoboza.global.Response;
import org.zerock.algoboza.provider.jwtToken.JwtTokenProvider;
import org.zerock.algoboza.domain.mypage.service.ConnectEmailService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/connection/email")
public class ConnectEmail {
    private final JwtTokenProvider jwtTokenProvider;
    private final ConnectEmailService connectEmailService;
    private final AuthService authService;


    public record editEmailRequest(
            List<emailList> edit_email
    ) {
    }

    @Builder
    public record emailList(
            String old_email,
            String new_email
    ) {
    }


    @PatchMapping("")
    public ResponseEntity<?> editEmail(@RequestBody editEmailRequest request) {

        String userEmail = authService.getUserContext().getEmail();

        connectEmailService.editConnectionEmail(userEmail, request.edit_email());

        return Response.builder()
                .status(HttpStatus.OK)
                .data(connectEmailService.ConnectionEmailStatus(userEmail))
                .massage("수정 됌")
                .build();
    }


    public record AddEmailRequest(
            List<String> email
    ) {
    }

    @PostMapping("")
    public Response<?> addEmail(@RequestBody AddEmailRequest request) {

        String userEmail = authService.getUserContext().getEmail();
        connectEmailService.addEmails(userEmail, request.email());


        return Response.builder()
                .status(HttpStatus.OK)
                .data(connectEmailService.ConnectionEmailStatus(userEmail))
                .massage("추가 완료")
                .build();
    }


    @GetMapping("")
    public Response<?> getEmail(@RequestHeader("Authorization") String accessToken) {
        String userEmail = authService.getUserContext().getEmail();

        return Response.builder()
                .status(HttpStatus.OK)
                .massage("조회 성공")
                .data(connectEmailService.ConnectionEmailStatus(userEmail))
                .build();

    }


}
