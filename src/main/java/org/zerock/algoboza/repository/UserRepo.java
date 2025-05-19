package org.zerock.algoboza.repository;

import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.zerock.algoboza.entity.UserEntity;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, Long> {

    UserEntity findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u.id FROM UserEntity u WHERE u.id != :excludedId")
    List<Long> findAllUserIds(@Param("excludedId") Long excludedId);

}
