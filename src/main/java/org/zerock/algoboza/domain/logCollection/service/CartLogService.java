package org.zerock.algoboza.domain.logCollection.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.zerock.algoboza.domain.logCollection.DTO.CartDTO;
import org.zerock.algoboza.domain.logCollection.DTO.CartDTO.CartItemDTO;
import org.zerock.algoboza.domain.logCollection.service.impl.LogProcessor;
import org.zerock.algoboza.entity.EmailIntegrationEntity;
import org.zerock.algoboza.entity.UserEntity;
import org.zerock.algoboza.entity.logs.EventEntity;
import org.zerock.algoboza.entity.logs.cart.CartItemCategoryEntity;
import org.zerock.algoboza.entity.logs.cart.CartItemEntity;
import org.zerock.algoboza.entity.logs.cart.CartItemSeasonEntity;
import org.zerock.algoboza.repository.logs.cartRepo.CartItemCategoryRepo;
import org.zerock.algoboza.repository.logs.cartRepo.CartItemRepo;
import org.zerock.algoboza.repository.logs.cartRepo.CartItemSeasonRepo;

@Service
@RequiredArgsConstructor
public class CartLogService implements LogProcessor<CartDTO> {
    private final BaseLogService baseLogService;
    private final CartItemRepo cartItemRepo;
    private final CartItemCategoryRepo cartItemCategoryRepo;
    private final CartItemSeasonRepo cartItemSeasonRepo;

    // 아이템 카테고리 저장
    public void saveItemCategory(CartItemEntity cartItemEntity, String category) {
        CartItemCategoryEntity cartItemCategoryEntity = CartItemCategoryEntity.builder()
                .cartItem(cartItemEntity)
                .categoryName(category)
                .build();

        cartItemCategoryRepo.save(cartItemCategoryEntity);

    }

    // 아이템 시즌 저장
    public void saveItemSeason(CartItemEntity cartItemEntity, String season) {
        CartItemSeasonEntity cartItemSeasonEntity = CartItemSeasonEntity.builder()
                .cartItem(cartItemEntity)
                .season(season)
                .build();
        cartItemSeasonRepo.save(cartItemSeasonEntity);
    }

    @Override
    public void saveTypeLog(EmailIntegrationEntity user, CartDTO log) {
        EventEntity eventEntity = baseLogService.saveBaseLog(user, log); // 기본 저장

        List<CartItemDTO> cartItemDTOList = log.getCart();
        cartItemDTOList.forEach(cartItemDTO -> {
            CartItemEntity cartItemEntity = CartItemEntity.builder()
                    .event(eventEntity)
                    .brand(cartItemDTO.getBrand())
                    .price(cartItemDTO.getPrice())
                    .quantity(cartItemDTO.getQuantity())
                    .discountPrice(cartItemDTO.getDiscountPrice())
                    .productId(cartItemDTO.getId())
                    .productName(cartItemDTO.getName())
                    .build();
            cartItemRepo.save(cartItemEntity);
            List<String> categoryList = cartItemDTO.getCategory();
            categoryList.forEach(categoryName -> {
                saveItemCategory(cartItemEntity, categoryName);
            });

            List<String> seasonList = cartItemDTO.getSeason();
            seasonList.forEach(seasonName -> {
                saveItemSeason(cartItemEntity, seasonName);
            });

        });

    }

    @Override
    public void readLog(UserEntity user, CartDTO log) {

    }
}
