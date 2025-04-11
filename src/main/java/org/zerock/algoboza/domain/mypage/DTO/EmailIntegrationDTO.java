package org.zerock.algoboza.domain.mypage.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.zerock.algoboza.entity.UserEntity;

import java.time.LocalDateTime;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailIntegrationDTO {
    private Long id;
    private UserEntity user;
    private String email;
    private String platform;
    private LocalDateTime creatAt;
}
