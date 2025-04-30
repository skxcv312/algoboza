package org.zerock.algoboza.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.algoboza.entity.EmailIntegrationEntity;
import org.zerock.algoboza.entity.UserEntity;

import java.util.List;

@Repository
public interface EmailIntegrationRepo extends JpaRepository<EmailIntegrationEntity, Long> {
    boolean existsByEmail(String email);

    List<EmailIntegrationEntity> findByUser(UserEntity user);

    EmailIntegrationEntity findByUserAndEmail(UserEntity user, String email);

    EmailIntegrationEntity findByEmail(String email);

}
