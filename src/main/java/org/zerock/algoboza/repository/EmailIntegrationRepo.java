package org.zerock.arcteryx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.arcteryx.entity.EmailIntegration;
import org.zerock.arcteryx.entity.UserEntity;

import java.util.List;

public interface EmailIntegrationRepo extends JpaRepository<EmailIntegration, Long> {
    public boolean existsByEmail(String email);

    public List<EmailIntegration> findByUser(UserEntity user);

    public EmailIntegration findByUserAndEmail(UserEntity user, String email);

    public EmailIntegration findByEmail(String email);

}
