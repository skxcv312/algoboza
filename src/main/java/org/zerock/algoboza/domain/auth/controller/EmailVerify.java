package org.zerock.algoboza.domain.auth.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.algoboza.global.Response;
import org.zerock.algoboza.domain.auth.service.EmailAuthService;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
public class EmailVerify {
    private final EmailAuthService emailAuthService;

    public record verifyEmailDTO(
            String email
    ) {
    }

    // 임시코드 생성후 리턴
    @PostMapping("/email/verify")
    public ResponseEntity<?> sendVerificationEmail(@RequestBody verifyEmailDTO verifyEmailDto) {
        // email 보내기
        String code = emailAuthService.sendMail(verifyEmailDto.email);

        Map<String, String> data = new HashMap<>();
        data.put("code", code);

        return Response.builder()
                .data(data)
                .status(HttpStatus.OK)
                .message("Verification email sent")
                .build();

    }
}
