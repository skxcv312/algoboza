package org.zerock.arcteryx.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.zerock.arcteryx.domain.mypage.DTO.EmailIntegrationDTO;
import org.zerock.arcteryx.global.BaseTimeEntity;

import java.time.Instant;
import java.util.Date;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmailIntegration extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private UserEntity user;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String platform;

    public EmailIntegrationDTO toDTO() {
        return EmailIntegrationDTO.builder()
                .id(this.id)
                .email(this.email)
                .platform(this.platform)
                .user(this.user)
                .build();

    }

}
