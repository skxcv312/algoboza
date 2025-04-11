package org.zerock.algoboza.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.zerock.algoboza.domain.mypage.DTO.EmailIntegrationDTO;
import org.zerock.algoboza.global.BaseTimeEntity;

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
