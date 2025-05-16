package org.zerock.algoboza.repository.logs.PlaceRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.algoboza.entity.logs.place.PlaceDetailEntity;

@Repository
public interface PlaceDetailRepo extends JpaRepository<PlaceDetailEntity, Long> {
}
