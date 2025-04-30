package org.zerock.algoboza.domain.mypage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.zerock.algoboza.domain.auth.DTO.UserDTO;
import org.zerock.algoboza.domain.auth.service.AuthService;
import org.zerock.algoboza.domain.mypage.DTO.GetMyInfoResponse;
import org.zerock.algoboza.domain.mypage.controller.ConnectEmail;
import org.zerock.algoboza.domain.mypage.controller.UserInfo;
import org.zerock.algoboza.entity.EmailIntegrationEntity;
import org.zerock.algoboza.entity.UserEntity;
import org.zerock.algoboza.repository.EmailIntegrationRepo;
import org.zerock.algoboza.repository.UserRepo;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserInfoService {
    private final AuthService authService;
    private final ConnectEmailService connectEmailService;
    private final EmailIntegrationRepo emailIntegrationRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;

    // 회원정보 수정
    public UserDTO editUserInfo(UserInfo.editMyPageRequest newUserInfo) {
        UserEntity oldUser = authService.getUserContext();
        //연동된 이메일 수정
        ConnectEmail.emailList emailList = ConnectEmail.emailList.builder()
                .new_email(newUserInfo.email())
                .old_email(oldUser.getEmail())
                .build();

        connectEmailService.editConnectionEmail(oldUser.getEmail(), List.of(emailList));

        // 정보 수정
        UserEntity user = userRepo.findById(oldUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("User ID not found"));
        user.setName(newUserInfo.name());
        user.setEmail(newUserInfo.email());
        user.setBirthDate(newUserInfo.birthDate());
        userRepo.save(user);

        // 유저 정보 반환
        return user.toDTO();
    }

    // 비밀번호 수정
    public void changePassword(UserEntity userEntity, String oldPassword, String newPassword) {
        if (passwordEncoder.matches(oldPassword, userEntity.getPassword())) {
            userEntity.setPassword(passwordEncoder.encode(newPassword));
            userRepo.save(userEntity);
        } else {
            throw new IllegalArgumentException("Old password does not match");
        }
    }


    public GetMyInfoResponse getMyInfo(UserEntity userEntity) {

        List<EmailIntegrationEntity> emailIntegrationEntityList = emailIntegrationRepo.findByUser(userEntity);

        List<GetMyInfoResponse.ConnectionEmail> ConnectionEmailList = emailIntegrationEntityList.stream()
                .map(emailIntegrationEntity -> GetMyInfoResponse.ConnectionEmail.builder()
                        .platform(emailIntegrationEntity.getPlatform())
                        .email(emailIntegrationEntity.getEmail())
                        .created_at(emailIntegrationEntity.getCreatedAt())
                        .build()
                ).toList();

        return GetMyInfoResponse.builder()
                .birth_date(userEntity.getBirthDate())
                .email(userEntity.getEmail())
                .name(userEntity.getName())
                .creat_at(userEntity.getCreatedAt())
                .connection_email(ConnectionEmailList)
                .build();
    }
}

