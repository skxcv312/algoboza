package org.zerock.algoboza.repository.logs.searchRepo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.algoboza.entity.logs.search.SearchEntity;

@Repository
public interface SearchRepo extends JpaRepository<SearchEntity, Long> {
    Optional<SearchEntity> findByEventId(Long eventId);
}
