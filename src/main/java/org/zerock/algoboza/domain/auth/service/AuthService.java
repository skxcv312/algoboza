package org.zerock.algoboza.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.zerock.algoboza.domain.auth.DTO.UserDTO;
import org.zerock.algoboza.domain.auth.controller.Signup;
import org.zerock.algoboza.entity.UserEntity;
import org.zerock.algoboza.repository.UserRepo;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public boolean userEmailExist(String email) {
        return userRepo.existsByEmail(email);
    }

    public boolean isNotEmail(String email) {
        return email == null || !email.contains("@");
    }

    public UserEntity findUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }


    public UserDTO matchesPassword(String email, String password) {
        UserEntity userEntity = findUserByEmail(email);
        if (passwordEncoder.matches(password, userEntity.getPassword())) {
            return userEntity.toDTO();
        }
        return null;
    }

    public UserDTO userSave(Signup.signupDTO signupDTO) {
        if (isNotEmail(signupDTO.email())) {
            throw new IllegalArgumentException("Email address is incorrect");
        }

        UserEntity user = UserEntity.builder()
                .email(signupDTO.email())
                .password(passwordEncoder.encode(signupDTO.password()))
                .name(signupDTO.name())
                .birthDate(signupDTO.birthdate())
                .build();

        return userRepo.save(user).toDTO();
    }

    // 유저 삭제
    public void deleteUser(UserDTO userDTO) {
        userRepo.deleteById(userDTO.getId());
    }
}
