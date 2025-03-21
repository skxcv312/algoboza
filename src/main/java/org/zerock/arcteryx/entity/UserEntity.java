package org.zerock.arcteryx.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Log4j2
@Entity
@NoArgsConstructor
@Getter
@Table(name = "User")
public class UserEntity implements UserDetails {

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

    @Column(nullable = true)
    private String createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Date.from(Instant.now()).toString();
    }


    @Builder
    public UserEntity(String email, String password, String name, String birthDate) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.birthDate = birthDate;
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
