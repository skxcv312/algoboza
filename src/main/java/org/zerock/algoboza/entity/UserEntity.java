package org.zerock.algoboza.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.zerock.algoboza.domain.auth.DTO.UserDTO;
import org.zerock.algoboza.global.BaseTimeEntity;

import java.util.Collection;
import java.util.List;

@Log4j2
@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "User")
public class UserEntity extends BaseTimeEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String birthDate;

    @Builder
    public UserEntity(String email, String password, String name, String birthDate) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.birthDate = birthDate;
    }

    public UserDTO toDTO() {
        return UserDTO.builder()
                .id(this.id)
                .email(this.email)
                .name(this.name)
                .birthDate(this.birthDate)
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
