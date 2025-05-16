package org.zerock.algoboza.domain.recommend.interestTracking.Service.Ecommerce;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.algoboza.domain.recommend.interestTracking.DTO.KeywordScoreDTO;
import org.zerock.algoboza.domain.recommend.interestTracking.Service.core.LogActionWeight;
import org.zerock.algoboza.domain.recommend.interestTracking.Service.core.WeightingInterest;
import org.zerock.algoboza.entity.logs.ClickTrackingEntity;
import org.zerock.algoboza.entity.logs.EventEntity;
import org.zerock.algoboza.entity.logs.category.CategoryEntity;
import org.zerock.algoboza.entity.logs.product.ProductEntity;
import org.zerock.algoboza.repository.logs.ClickTrackingRepo;
import org.zerock.algoboza.repository.logs.EventRepo;
import org.zerock.algoboza.repository.logs.categoryRepo.CategoryRepo;
import org.zerock.algoboza.repository.logs.productRepo.ProductRepo;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductInterestService extends WeightingInterest {
    private final CategoryRepo categoryRepo;
    private final ProductRepo productRepo;
    private final EventRepo eventRepo;
    private final ClickTrackingRepo clickTrackingRepo;

    private final LogActionWeight PRODUCT = LogActionWeight.PRODUCT;
    private final LogActionWeight MORE_SEE = LogActionWeight.MORE_SEE;
    private final double LIKE_WEIGHT = LogActionWeight.LIKE.getWeights();

    @Override
    protected List<EventEntity> getEventsByRepository(Long id) {
        return eventRepo.findByEventTypeAndEmailIntegrationUserId(PRODUCT.getAction(), id);
    }

    // 키워드 선별과 점수 계산
    @Override
    protected List<KeywordScoreDTO> keywordCalculation(EventEntity event) {
        List<KeywordScoreDTO> results = new ArrayList<>();
        ProductEntity productEntity = findProductEntity(event.getId());

        double score = calculateScore(productEntity, event.getId());
        List<String> keywords = createKeyword(event.getId());
        results.addAll(createKeywordScoreList(keywords, score));

        return results;
    }

    // 제품 엔티티 조회
    private ProductEntity findProductEntity(Long eventId) {
        return productRepo.findByEventId(eventId)
                .orElseThrow(() -> new RuntimeException("ProductEntity is null"));
    }

    // 점수 계산 메서드
    private double calculateScore(ProductEntity productEntity, Long eventId) {
        double score = PRODUCT.getWeights();

        // 좋아요 여부 점수 추가
        if (productEntity.isLike()) {
            score += LIKE_WEIGHT;
        }

        // 추가 클릭 점수 계산
        List<ClickTrackingEntity> clickTrackingEntities = clickTrackingRepo.findByEventId(eventId)
                .orElseThrow(() -> new RuntimeException("ClickTrackingEntity is null"));

        score += clickTrackingEntities.stream()
                .filter(click -> click.getAction().equals(MORE_SEE.getAction()))
                .count() * MORE_SEE.getWeights();

        return score;
    }

    // 키워드 선정
    private List<String> createKeyword(Long eventId) {
        List<CategoryEntity> categoryEntities = categoryRepo.findByEventId(eventId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return categoryEntities.stream().map(CategoryEntity::getCategory).toList();
    }

    // 키워드 점수 리스트 생성
    private List<KeywordScoreDTO> createKeywordScoreList(List<String> keywordList, double score) {
        List<KeywordScoreDTO> results = new ArrayList<>();

        keywordList.forEach(keyword ->
                results.add(new KeywordScoreDTO(keyword, score))
        );

        return results;
    }
}
