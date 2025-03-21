package org.zerock.arcteryx.controller.auth;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.arcteryx.config.Response;
import org.zerock.arcteryx.provider.jwtToken.JwtTokenDTO;
import org.zerock.arcteryx.service.auth.RefreshTokenService;

@RequiredArgsConstructor
@RestController
public class RefreshToken {
    private final RefreshTokenService refreshTokenService;

    @GetMapping("refresh-token")
    public ResponseEntity<?> getRefreshToken(@RequestHeader("RefreshToken") String refreshToken) {

        // 재발급
        JwtTokenDTO newToken = refreshTokenService.getNewToken(refreshToken);

        if(newToken == null) {
            return Response.builder()
                    .status(HttpStatus.FORBIDDEN)
                    .massage("Invalid refresh token")
                    .build();
        }

        return Response.builder()
                .status(HttpStatus.OK)
                .massage("Refresh token expired")
                .data(newToken)
                .build();

    }
}
