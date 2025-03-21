package org.zerock.arcteryx.service.auth;


import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zerock.arcteryx.entity.UserEntity;
import org.zerock.arcteryx.provider.jwtToken.JwtTokenDTO;
import org.zerock.arcteryx.provider.jwtToken.JwtTokenProvider;
import org.zerock.arcteryx.repository.UserRepo;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepo userRepo;

    // 재발급
    public JwtTokenDTO getNewToken(String refreshToken) {
        try{
            String userEmail = jwtTokenProvider.getUserEmail(refreshToken);
            UserEntity user = userRepo.findByEmail(userEmail);
            return jwtTokenProvider.createToken(user);

        }
        catch(JwtException jwtException ){
            log.info("refresh token exception", jwtException);
            return null;
        }

    }


}
