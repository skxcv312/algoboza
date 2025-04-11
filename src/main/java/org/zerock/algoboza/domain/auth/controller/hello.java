package org.zerock.algoboza.domain.auth.controller;


import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.algoboza.global.Response;

@RestController
@Log4j2
public class hello {
    @GetMapping("/hello")
    public ResponseEntity<?> hello() {
        return Response.builder()
                .status(HttpStatus.OK)
                .message("Hello World")
                .build();
    }
}
