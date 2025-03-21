package org.zerock.arcteryx.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.zerock.arcteryx.controller.auth.Login;
import org.zerock.arcteryx.controller.auth.Signup;
import org.zerock.arcteryx.entity.UserEntity;
import org.zerock.arcteryx.repository.UserRepo;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public boolean userEmailExist(String email) {
        return userRepo.existsByEmail(email);
    }

    public UserEntity matchesPassword(Login.loginDTO loginDto) {
        UserEntity user = userRepo.findByEmail(loginDto.email());
         if(passwordEncoder.matches(loginDto.password(), user.getPassword())){
             return user;
         }
         return null;
    }

    public UserEntity userSave(Signup.signupDTO signupDTO) {
        log.info("signupDTO : " +  signupDTO);
        UserEntity user = UserEntity.builder()
                .email(signupDTO.email())
                .password(passwordEncoder.encode(signupDTO.password()))
                .name(signupDTO.name())
                .birthDate(signupDTO.birthdate())
                .build();

        return userRepo.save(user);
    }

}
