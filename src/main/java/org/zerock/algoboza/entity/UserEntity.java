package org.zerock.algoboza.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.zerock.algoboza.domain.auth.DTO.UserDTO;
import org.zerock.algoboza.entity.base.BaseEntity;

import java.util.Collection;
import java.util.List;

@Log4j2
@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Table(name = "User")
public class UserEntity extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // private → protected 변경

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String birthDate;

    public UserDTO toDTO() {
        return UserDTO.builder()
                .id(this.id)
                .email(this.email)
                .name(this.name)
                .birthDate(this.birthDate)
                .createdAt(this.createdAt)
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
