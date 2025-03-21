package org.zerock.arcteryx.controller.auth;


import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.arcteryx.config.Response;

@RestController
@Log4j2
public class hello {
    @GetMapping("/hello")
    public ResponseEntity<?> hello(){
        return Response.builder()
                .status(HttpStatus.OK)
                .massage("Hello World")
                .build();
    }
}
