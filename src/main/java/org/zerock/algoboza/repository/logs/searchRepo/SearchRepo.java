package org.zerock.algoboza.repository.logs.searchRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.algoboza.entity.logs.search.SearchEntity;

@Repository
public interface SearchRepo extends JpaRepository<SearchEntity, Long> {
    SearchEntity findByEventId(Long eventId);
}
