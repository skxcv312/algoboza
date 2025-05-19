package org.zerock.algoboza.domain.recommend.interestTracking.Service.Ecommerce;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.algoboza.domain.recommend.interestTracking.DTO.KeywordScoreDTO;
import org.zerock.algoboza.domain.recommend.interestTracking.Service.core.LogActionWeight;
import org.zerock.algoboza.domain.recommend.interestTracking.Service.core.WeightingInterest;
import org.zerock.algoboza.entity.logs.ClickTrackingEntity;
import org.zerock.algoboza.entity.logs.DetailsEntity;
import org.zerock.algoboza.entity.logs.EventEntity;
import org.zerock.algoboza.entity.logs.ViewEntity;
import org.zerock.algoboza.entity.logs.category.CategoryEntity;
import org.zerock.algoboza.entity.logs.product.ProductEntity;
import org.zerock.algoboza.repository.logs.ClickTrackingRepo;
import org.zerock.algoboza.repository.logs.DetailsRepo;
import org.zerock.algoboza.repository.logs.EventRepo;
import org.zerock.algoboza.repository.logs.ViewRepo;
import org.zerock.algoboza.repository.logs.categoryRepo.CategoryRepo;
import org.zerock.algoboza.repository.logs.productRepo.ProductRepo;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductInterestService extends WeightingInterest {
    private final CategoryRepo categoryRepo;
    private final ProductRepo productRepo;
    private final EventRepo eventRepo;
    private final ViewRepo viewRepo;
    private final DetailsRepo detailsRepo;
    private final ClickTrackingRepo clickTrackingRepo;

    private final LogActionWeight PRODUCT = LogActionWeight.PRODUCT;
    private final LogActionWeight MORE_SEE = LogActionWeight.CLICK_MORE_SEE;
    private final double LIKE_WEIGHT = LogActionWeight.LIKE.getWeights();
    private final LogActionWeight CLICK_CART = LogActionWeight.CLICK_CART;

    @Override
    protected List<EventEntity> getEventsByRepository(Long id) {
        return eventRepo.findByEventTypeAndEmailIntegrationUserId(PRODUCT.getAction(), id);
    }

    // 키워드 선별과 점수 계산
    @Override
    protected List<KeywordScoreDTO> keywordCalculation(EventEntity event) {
        List<KeywordScoreDTO> results = new ArrayList<>();

        double score = calculateScore(event);
        List<String> keywords = createKeyword(event.getId());
        results.addAll(createKeywordScoreList(keywords, score));

        return results;
    }

    private ViewEntity findViewEntity(Long eventId) {
        return viewRepo.findByEventId(eventId).orElseThrow(() -> new RuntimeException("ViewEntity is null"));
    }

    // 제품 엔티티 조회
    private ProductEntity findProductEntity(Long eventId) {
        return productRepo.findByEventId(eventId)
                .orElseThrow(() -> new RuntimeException("ProductEntity is null"));
    }

    // 점수 계산 메서드
    private double calculateScore(EventEntity event) {
        ProductEntity productEntity = findProductEntity(event.getId());
        ViewEntity viewEntity = findViewEntity(event.getId());

        double score = PRODUCT.getWeights();

        // 좋아요 여부 점수 추가
        if (productEntity.isLike()) {
            score += LIKE_WEIGHT;
        }

        // 추가 클릭 점수 계산
        List<ClickTrackingEntity> clickTrackingEntities = clickTrackingRepo.findByEventId(event.getId())
                .orElseThrow(() -> new RuntimeException("ClickTrackingEntity is null"));

        double clickCartScore = 0;
        double moreSeeScore = 0;
        for (ClickTrackingEntity clickTrackingEntity : clickTrackingEntities) {

            if (clickTrackingEntity.getAction().equals(MORE_SEE.getAction())) {
                moreSeeScore = MORE_SEE.getWeights();
            }
            if (clickTrackingEntity.getAction().equals(CLICK_CART.getAction())) {
                if (Duration.between(viewEntity.getStartTime(), clickTrackingEntity.getClickTime()).toMinutes() >= 7) {
                    clickCartScore = CLICK_CART.getWeights();
                }

            }
        }

        score += clickCartScore + moreSeeScore;

        return score;
    }

    // 키워드 선정
    private List<String> createKeyword(Long eventId) {
        List<CategoryEntity> categoryEntities = categoryRepo.findByEventId(eventId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        List<String> categoryNames = new ArrayList<>(
                categoryEntities.stream().map(CategoryEntity::getCategory)
                        .skip(Math.max(0, categoryEntities.size() - 1))
                        .toList());
        List<DetailsEntity> detailsEntities = detailsRepo.findByEventId(eventId);
        String details = detailsEntities.stream().map(DetailsEntity::getDetail).toList().getFirst();

        if (List.of("남", "여", "공용").contains(details)) {
            categoryNames = categoryNames.stream()
                    .map(v -> details + " " + v)
                    .toList();
        }
        return categoryNames;
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
