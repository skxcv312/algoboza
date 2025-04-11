package org.zerock.algoboza.domain.auth.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.algoboza.global.Response;

@RestController
public class test {

    @GetMapping("/")
    public ResponseEntity<?> test() {
        return Response.builder()
                .status(HttpStatus.OK)
                .massage("넌 2번째 성공이다")
                .build();
    }
}
