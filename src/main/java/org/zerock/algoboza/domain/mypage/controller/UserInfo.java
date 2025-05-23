package org.zerock.algoboza.domain.mypage.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.zerock.algoboza.domain.auth.DTO.UserDTO;
import org.zerock.algoboza.domain.auth.service.AuthService;
import org.zerock.algoboza.domain.mypage.DTO.GetMyInfoResponse;
import org.zerock.algoboza.global.Response;
import org.zerock.algoboza.entity.UserEntity;
import org.zerock.algoboza.provider.jwtToken.JwtTokenDTO;
import org.zerock.algoboza.provider.jwtToken.JwtTokenProvider;
import org.zerock.algoboza.domain.mypage.service.UserInfoService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/my-page")
@Log4j2
public class UserInfo {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserInfoService userInfoService;
    private final AuthService authService;


    // 회원 정보
    @GetMapping
    public Response<?> getMyInfo(
            @AuthenticationPrincipal UserEntity user
    ) {
        GetMyInfoResponse response = userInfoService.getMyInfo(user);
        return Response.builder()
                .status(HttpStatus.OK)
                .message("조회 성공")
                .data(response)
                .build();
    }

    // 회원 정보 수정
    public record editMyPageRequest(
            String email,
            String name,
            String birthDate
    ) {
    }

    @PatchMapping("/change")
    public Response<?> editMyInfo(
            @AuthenticationPrincipal UserEntity user,
            @RequestBody editMyPageRequest request) {
        UserDTO newUserDTO = userInfoService.editUserInfo(user, request);
        JwtTokenDTO newToken = jwtTokenProvider.createToken(newUserDTO);
        HttpHeaders headers = jwtTokenProvider.setTokenToHeader(newToken);

        return Response.builder()
                .status(HttpStatus.OK)
                .headers(headers)
                .data(newUserDTO)
                .message("수정 완료")
                .build();
    }

    public record changePassword(
            String old_password,
            String new_password
    ) {
    }

    @PatchMapping("/change/password")
    public Response<?> editPassword(
            @AuthenticationPrincipal UserEntity oldUser,
            @RequestBody changePassword request) {
        userInfoService.changePassword(oldUser, request.old_password, request.new_password);

        return Response.builder()
                .status(HttpStatus.OK)
                .message("비밀번호 바뀜")
                .build();
    }
}
