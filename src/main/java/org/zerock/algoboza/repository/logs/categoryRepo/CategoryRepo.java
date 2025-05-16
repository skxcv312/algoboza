package org.zerock.algoboza.repository.logs.categoryRepo;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.algoboza.entity.logs.category.CategoryEntity;

@Repository
public interface CategoryRepo extends JpaRepository<CategoryEntity, Long> {
    Optional<List<CategoryEntity>> findByEventId(Long eventId);
}
