package org.zerock.algoboza.domain.mypage.service;


import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.algoboza.domain.auth.service.AuthService;
import org.zerock.algoboza.domain.mypage.DTO.EmailIntegrationDTO;
import org.zerock.algoboza.domain.mypage.controller.ConnectEmail;
import org.zerock.algoboza.entity.EmailIntegration;
import org.zerock.algoboza.entity.UserEntity;
import org.zerock.algoboza.repository.EmailIntegrationRepo;
import org.zerock.algoboza.repository.UserRepo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ConnectEmailService {
    private final UserRepo userRepo;
    private final EmailIntegrationRepo emailIntegrationRepo;
    private final AuthService authService;


    // 이메일 플랫폼 분리
    public String getPlatform(String email) {
        if (authService.isNotEmail(email)) { // 이메일 형식인지 확인
            throw new IllegalArgumentException("Invalid email format");
        }

        try {
            String domain = email.split("@")[1];       // e.g., "gmail.com"
            String platform = domain.split("\\.")[0];  // e.g., "gmail"
            return platform.toLowerCase();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get platform");
        }
    }


    // 이메일 추가 연결
    public void addEmails(String userEmail, List<String> newEmailList) {
        UserEntity user = userRepo.findByEmail(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        for (String newEmail : newEmailList) {
            if (emailIntegrationRepo.existsByEmail(newEmail)) {
                continue;
            }

            String platform = getPlatform(newEmail);

            EmailIntegration emailIntegration = EmailIntegration.builder()
                    .user(user)
                    .platform(platform)
                    .email(newEmail)
                    .build();

            emailIntegrationRepo.save(emailIntegration);
        }
        lookUpConnectionEmail(user.getEmail());
    }

    // 이메일 삭제
    public void deleteEmails(String email) {
        EmailIntegration emailIntegration = emailIntegrationRepo.findByEmail(email);
        if (emailIntegration == null) {
            throw new IllegalArgumentException("Email not found");
        }
        emailIntegrationRepo.delete(emailIntegration);
    }

    // 이메일 수정
    public void editConnectionEmail(String userEmail, List<ConnectEmail.emailList> editEmailRequestList) {
        UserEntity user = userRepo.findByEmail(userEmail);

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        for (ConnectEmail.emailList dto : editEmailRequestList) {
            // 새로운 이메일이 널인경우 삭제로 본다
            if (dto.new_email() == null) {
                deleteEmails(dto.old_email());
                continue;
            }

            EmailIntegration emailIntegration = emailIntegrationRepo
                    .findByUserAndEmail(user, dto.old_email());

            if (emailIntegration == null) {
                throw new IllegalArgumentException("Email not found: " + dto.old_email());
            }

            emailIntegration.setEmail(dto.new_email());
            emailIntegration.setPlatform(getPlatform(dto.new_email()));
        }

    }

    // 연결된 이메일 조회
    public List<EmailIntegrationDTO> lookUpConnectionEmail(String userEmail) {
        UserEntity user = userRepo.findByEmail(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        return emailIntegrationRepo.findByUser(user).stream()
                .map(integration -> EmailIntegrationDTO.builder()
                        .user(user)
                        .creatAt(integration.getCreatedAt())
                        .platform(integration.getPlatform())
                        .email(integration.getEmail())
                        .build())
                .toList();
    }


    @Builder
    public record emailListResponse(
            String user_name,
            String user_email,
            List<emailListResponse.emailDetail> email_list
    ) {
        @Builder
        public record emailDetail(
                String connection_email,
                String platform,
                LocalDateTime creat_at
        ) {
        }
    }

    public emailListResponse ConnectionEmailStatus(String userEmail) {
        List<EmailIntegrationDTO> emailIntegrationDTOList = lookUpConnectionEmail(userEmail);

        List<emailListResponse.emailDetail> emailDetails = new ArrayList<>();
        for (EmailIntegrationDTO dto : emailIntegrationDTOList) {
            emailListResponse.emailDetail detail = emailListResponse.emailDetail.builder()
                    .connection_email(dto.getEmail())
                    .platform(dto.getPlatform())
                    .creat_at(dto.getCreatAt())
                    .build();

            emailDetails.add(detail);
        }

        return emailListResponse.builder()
                .user_email(userEmail)
                .user_name(emailIntegrationDTOList.getFirst().getUser().getName())
                .email_list(emailDetails)
                .build();
    }


}
