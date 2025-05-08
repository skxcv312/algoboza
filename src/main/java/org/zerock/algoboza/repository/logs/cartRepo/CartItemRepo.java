package org.zerock.algoboza.repository.logs.cartRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.algoboza.entity.logs.cart.CartItemEntity;


@Repository
public interface CartItemRepo extends JpaRepository<CartItemEntity, Long> {
    CartItemEntity findByEventId(Long eventId);
}
