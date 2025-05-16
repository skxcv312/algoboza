package org.zerock.algoboza.repository.logs.PlaceRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.algoboza.entity.logs.place.PlaceEntity;

@Repository
public interface PlaceRepo extends JpaRepository<PlaceEntity, Long> {
    PlaceEntity findByEventId(Long eventId);
}
