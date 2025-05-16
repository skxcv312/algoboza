package org.zerock.algoboza.repository.logs.productRepo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.algoboza.entity.logs.product.ProductEntity;

@Repository
public interface ProductRepo extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findByEventId(Long eventId);
}
