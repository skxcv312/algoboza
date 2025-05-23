package org.zerock.algoboza.repository.logs;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.algoboza.entity.logs.ClickTrackingEntity;

@Repository
public interface ClickTrackingRepo extends JpaRepository<ClickTrackingEntity, Long> {
    Optional<List<ClickTrackingEntity>> findByEventId(Long eventId);
}
