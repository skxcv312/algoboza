package org.zerock.algoboza.repository.logs.cartRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.algoboza.entity.logs.cart.CartItemSeasonEntity;

@Repository
public interface CartItemSeasonRepo extends JpaRepository<CartItemSeasonEntity, Long> {
}
