package org.zerock.algoboza.repository.logs.cartRepo;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.algoboza.entity.logs.cart.CartItemEntity;


@Repository
public interface CartItemRepo extends JpaRepository<CartItemEntity, Long> {
    Optional<List<CartItemEntity>> findByEventId(Long eventId);
}
