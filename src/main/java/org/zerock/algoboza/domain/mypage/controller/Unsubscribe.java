package org.zerock.algoboza.domain.mypage.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.algoboza.domain.auth.DTO.UserDTO;
import org.zerock.algoboza.domain.auth.service.AuthService;
import org.zerock.algoboza.entity.UserEntity;
import org.zerock.algoboza.global.Response;

@RestController
@RequiredArgsConstructor
public class Unsubscribe {

    private final AuthService authService;

    @GetMapping("/my-page/unsubscribe")
    public Response<?> unsubscribe(
            @AuthenticationPrincipal UserEntity user
    ) {
        UserDTO userDTO = user.toDTO();
        authService.deleteUser(userDTO);

        return Response.builder()
                .status(HttpStatus.OK)
                .message("Unsubscribed")
                .headers(new HttpHeaders())
                .build();
    }

}
