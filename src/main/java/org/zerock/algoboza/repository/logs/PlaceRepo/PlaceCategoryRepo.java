package org.zerock.algoboza.repository.logs.PlaceRepo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.algoboza.entity.logs.place.PlaceCategoryEntity;

@Repository
public interface PlaceCategoryRepo extends JpaRepository<PlaceCategoryEntity, Long> {

    List<PlaceCategoryEntity> findByPlaceDetailId(Long placeId);
}
