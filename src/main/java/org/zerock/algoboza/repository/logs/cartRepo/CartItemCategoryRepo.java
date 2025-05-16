package org.zerock.algoboza.repository.logs.cartRepo;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.algoboza.entity.logs.cart.CartItemCategoryEntity;

@Repository
public interface CartItemCategoryRepo extends JpaRepository<CartItemCategoryEntity, Long> {
    Optional<List<CartItemCategoryEntity>> findByCartItem_Id(Long cartItemId);
}
