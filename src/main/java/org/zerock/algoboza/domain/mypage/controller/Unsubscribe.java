package org.zerock.arcteryx.domain.mypage.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.arcteryx.domain.auth.DTO.UserDTO;
import org.zerock.arcteryx.domain.auth.service.AuthService;
import org.zerock.arcteryx.global.Response;

@RestController
@RequiredArgsConstructor
public class Unsubscribe {

    private final AuthService authService;

    @GetMapping("/my-page/unsubscribe")
    public Response<?> unsubscribe() {
        UserDTO userDTO = authService.getUserContext().toDTO();
        authService.deleteUser(userDTO);
        
        return Response.builder()
                .status(HttpStatus.OK)
                .massage("Unsubscribed")
                .headers(new HttpHeaders())
                .build();
    }

}
