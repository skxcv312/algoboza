package org.zerock.arcteryx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.arcteryx.entity.UserEntity;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, Long>  {

    UserEntity findByEmail(String email);

    boolean existsByEmail(String email);

}
