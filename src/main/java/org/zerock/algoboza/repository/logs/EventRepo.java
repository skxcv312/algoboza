package org.zerock.algoboza.repository.logs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.algoboza.entity.logs.EventEntity;

@Repository
public interface EventRepo extends JpaRepository<EventEntity, Long> {
    EventEntity findByEventType(String eventType);
}
