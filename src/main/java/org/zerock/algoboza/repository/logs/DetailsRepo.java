package org.zerock.algoboza.repository.logs;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.algoboza.entity.logs.DetailsEntity;

@Repository
public interface DetailsRepo extends JpaRepository<DetailsEntity, Long> {
    List<DetailsEntity> findByEventId(Long eventId);
}
