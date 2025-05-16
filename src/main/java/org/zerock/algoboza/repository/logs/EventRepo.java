package org.zerock.algoboza.repository.logs;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.algoboza.entity.logs.EventEntity;

@Repository
public interface EventRepo extends JpaRepository<EventEntity, Long> {
    List<EventEntity> findByEventTypeAndEmailIntegrationUserId(String eventType, Long userId);


    Optional<List<EventEntity>> findByEmailIntegrationUserId(Long aLong);

    void deleteByEmailIntegrationUserId(Long userId);


    List<EventEntity> findTop20ByEventTypeAndEmailIntegrationUserIdOrderByCreatedAtDesc(String action, Long id);
}
