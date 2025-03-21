package org.zerock.arcteryx.controller.auth;


import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.arcteryx.config.Response;
import org.zerock.arcteryx.entity.UserEntity;
import org.zerock.arcteryx.provider.jwtToken.JwtTokenDTO;
import org.zerock.arcteryx.provider.jwtToken.JwtTokenProvider;
import org.zerock.arcteryx.service.auth.AuthService;


@Slf4j
@RequiredArgsConstructor
@RestController
public class Signup {
   private final Gson gson;
   private final AuthService authService;
   private final JwtTokenProvider jwtTokenProvider;


   public record signupDTO(
        String email,
        String password,
        String name,
        String birthdate
   ){}


   @PostMapping("/signup")
   public Response<?> signup(@RequestBody signupDTO signupDTO) {
       // email 검증
        log.info("Signup Request"+ gson.toJson(signupDTO));

       // 유저 정보 존재
       if(authService.userEmailExist(signupDTO.email())) {
           return Response.builder()
                   .status(HttpStatus.BAD_REQUEST)
                   .massage("가입된 유저")
                   .build();
       }

       // user save
       UserEntity user = authService.userSave(signupDTO);


       // creat token
       JwtTokenDTO jwtTokenDTO = jwtTokenProvider.createToken(user);
       HttpHeaders headers = jwtTokenProvider.setTokenToHeader(jwtTokenDTO);

       return Response.builder()
               .status(HttpStatus.CREATED)
               .massage("가입완료")
               .headers(headers)
               .data(jwtTokenDTO)
               .build();

   }
}
