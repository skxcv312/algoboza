package org.zerock.algoboza.domain.mypage.controller;


import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.zerock.algoboza.domain.auth.service.AuthService;
import org.zerock.algoboza.entity.UserEntity;
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
    public ResponseEntity<?> editEmail(
            @AuthenticationPrincipal UserEntity user,
            @RequestBody editEmailRequest request
    ) {

        String userEmail = user.getEmail();

        connectEmailService.editConnectionEmail(userEmail, request.edit_email());

        return Response.builder()
                .status(HttpStatus.OK)
                .data(connectEmailService.ConnectionEmailStatus(userEmail))
                .message("수정 됌")
                .build();
    }


    public record AddEmailRequest(
            List<String> email
    ) {
    }

    @PostMapping("")
    public Response<?> addEmail(
            @RequestBody AddEmailRequest request,
            @AuthenticationPrincipal UserEntity user
    ) {

        String userEmail = user.getEmail();
        connectEmailService.addEmails(userEmail, request.email());

        return Response.builder()
                .status(HttpStatus.OK)
                .data(connectEmailService.ConnectionEmailStatus(userEmail))
                .message("추가 완료")
                .build();
    }


    @GetMapping("")
    public Response<?> getEmail(
            @AuthenticationPrincipal UserEntity user
    ) {
        String userEmail = user.getEmail();

        return Response.builder()
                .status(HttpStatus.OK)
                .message("조회 성공")
                .data(connectEmailService.ConnectionEmailStatus(userEmail))
                .build();

    }


}
