package org.zerock.algoboza.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.zerock.algoboza.domain.mypage.DTO.EmailIntegrationDTO;
import org.zerock.algoboza.global.BaseEntity;

@Entity
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class EmailIntegration extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // private → protected 변경

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
                .creatAt(this.createdAt)
                .build();
    }
}
