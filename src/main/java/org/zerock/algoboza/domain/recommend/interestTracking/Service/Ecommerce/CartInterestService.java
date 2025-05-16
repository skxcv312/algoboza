package org.zerock.algoboza.domain.recommend.interestTracking.Service.Ecommerce;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.algoboza.domain.recommend.interestTracking.DTO.KeywordScoreDTO;
import org.zerock.algoboza.domain.recommend.interestTracking.Service.Ecommerce.core.LogActionWeight;
import org.zerock.algoboza.domain.recommend.interestTracking.Service.Ecommerce.core.WeightingInterest;
import org.zerock.algoboza.entity.UserEntity;
import org.zerock.algoboza.entity.logs.EventEntity;
import org.zerock.algoboza.entity.logs.cart.CartItemCategoryEntity;
import org.zerock.algoboza.entity.logs.cart.CartItemEntity;
import org.zerock.algoboza.repository.logs.EventRepo;
import org.zerock.algoboza.repository.logs.cartRepo.CartItemCategoryRepo;
import org.zerock.algoboza.repository.logs.cartRepo.CartItemRepo;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CartInterestService extends WeightingInterest {
    private final EventRepo eventRepo;
    private final CartItemCategoryRepo cartItemCategoryRepo;
    private final CartItemRepo cartItemRepo;

    private final LogActionWeight CART = LogActionWeight.CART;
    private final LogActionWeight QUANTITY = LogActionWeight.QUANTITY;

    // 카트 아이템 조회
    private List<CartItemEntity> findCartItemEntity(Long eventId) {
        log.debug("Finding CartItemEntity by eventId: {}", eventId);
        return cartItemRepo.findByEventId(eventId)
                .orElseThrow(() -> {
                    log.error("Cart item not found for eventId: {}", eventId);
                    return new RuntimeException("Cart item not found");
                });
    }

    // 카트 아이템 카테고리 조회
    private List<CartItemCategoryEntity> findCartItemCategoryEntity(Long cartItemId) {
        log.debug("Finding CartItemCategoryEntity by cartItemId: {}", cartItemId);
        return cartItemCategoryRepo.findByCartItem_Id(cartItemId)
                .orElseThrow(() -> {
                    log.error("Cart item category not found for cartItemId: {}", cartItemId);
                    return new RuntimeException("Cart item category not found");
                });
    }

    // 점수 계산
    private double calculateScore(CartItemEntity cartItemEntity) {
        double score = CART.getWeights();
        log.debug("Base score from CART weight: {}", score);

        double additionalScore = QUANTITY.getWeights() * cartItemEntity.getQuantity();
        score += additionalScore;
        log.debug("Added score from quantity ({} * {}): {}", QUANTITY.getWeights(), cartItemEntity.getQuantity(),
                additionalScore);

        log.info("Final calculated score: {}", score);
        return score;
    }

    // 키워드 목록 생성
    private List<String> createKeyword(List<CartItemCategoryEntity> cartItemCategoryEntities) {
        log.debug("Creating keywords from cart item categories");
        List<String> keywords = cartItemCategoryEntities.stream()
                .map(CartItemCategoryEntity::getCategoryName)
                .toList();
        log.info("Generated keywords: {}", keywords);
        return keywords;
    }

    // 키워드 점수 리스트 생성
    private List<KeywordScoreDTO> createKeywordScoreList(List<String> keywordList, double score) {
        log.debug("Creating keyword score list with score: {}", score);
        List<KeywordScoreDTO> results = new ArrayList<>();
        keywordList.forEach(keyword -> {
            log.debug("Adding keyword score - keyword: {}, score: {}", keyword, score);
            results.add(new KeywordScoreDTO(keyword, score));
        });
        log.info("Keyword score list created: {}", results);

        return results;
    }

    @Override
    protected List<EventEntity> getEventsByRepository(Long id) {
        List<EventEntity> events = eventRepo.findByEventTypeAndEmailIntegrationUserId(CART.getAction(), id);
        return events;
    }

    @Override
    protected List<KeywordScoreDTO> keywordCalculation(EventEntity event) {
        log.debug("Calculating keywords for eventId: {}", event.getId());
        List<KeywordScoreDTO> results = new ArrayList<>();

        List<CartItemEntity> cartItemEntityList = findCartItemEntity(event.getId());
        log.debug("Found CartItemEntity: {}", cartItemEntityList);

        for (CartItemEntity cartItemEntity : cartItemEntityList) {
            List<CartItemCategoryEntity> cartItemCategoryEntities = findCartItemCategoryEntity(
                    cartItemEntity.getId());
            double score = calculateScore(cartItemEntity);
            List<String> keywords = createKeyword(cartItemCategoryEntities);

            results.addAll(createKeywordScoreList(keywords, score));
        }
        log.info("Keyword calculation completed for eventId: {}", event.getId());
        return results;
    }
//    @Override
//    protected List<KeywordScoreDTO> keywordCalculation(EventEntity event) {
//        try {
//            log.debug("Calculating keywords for eventId: {}", event.getId());
//            // 실제 키워드 계산 로직
//            List<KeywordScoreDTO> keywordScores = new ArrayList<>();
//            // 가정된 계산 로직
//            keywordScores.add(new KeywordScoreDTO("example", 1.0));
//            log.debug("Keyword calculation successful for eventId: {}", event.getId());
//            return keywordScores;
//        } catch (Exception e) {
//            log.error("Error during keyword calculation for eventId: {} - {}", event.getId(), e.getMessage());
//            throw new RuntimeException("Keyword calculation failed", e);
//        }
//    }
}
