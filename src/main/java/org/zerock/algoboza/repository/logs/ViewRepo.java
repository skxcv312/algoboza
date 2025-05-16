package org.zerock.algoboza.repository.logs;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.algoboza.entity.logs.ViewEntity;

@Repository
public interface ViewRepo extends JpaRepository<ViewEntity, Long> {
    Optional<ViewEntity> findByEventId(long eventId);
}
