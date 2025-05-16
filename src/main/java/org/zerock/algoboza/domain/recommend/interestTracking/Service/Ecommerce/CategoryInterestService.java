package org.zerock.algoboza.domain.recommend.interestTracking.Service.Ecommerce;


import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.zerock.algoboza.domain.recommend.interestTracking.DTO.KeywordScoreDTO;
import org.zerock.algoboza.domain.recommend.interestTracking.Service.core.LogActionWeight;
import org.zerock.algoboza.domain.recommend.interestTracking.Service.core.WeightingInterest;
import org.zerock.algoboza.entity.logs.EventEntity;
import org.zerock.algoboza.entity.logs.category.CategoryEntity;
import org.zerock.algoboza.repository.logs.EventRepo;
import org.zerock.algoboza.repository.logs.categoryRepo.CategoryRepo;

@RequiredArgsConstructor
@Service
public class CategoryInterestService extends WeightingInterest {
    private final CategoryRepo categoryRepo;
    private final EventRepo eventRepo;

    private final LogActionWeight CATEGORY = LogActionWeight.CATEGORY;

    // 점수 계산 메서드
    private double calculateScore() {
        return CATEGORY.getWeights();
    }

    private List<CategoryEntity> findCategoryEntity(Long eventId) {
        return categoryRepo.findByEventId(eventId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    private List<String> createKeyword(List<CategoryEntity> categoryEntities) {
        return categoryEntities.stream().map(CategoryEntity::getCategory).toList();

    }

    private List<KeywordScoreDTO> createKeywordScoreList(List<String> keywordList, double score) {
        List<KeywordScoreDTO> results = new ArrayList<>();
        keywordList.forEach(keyword ->
                results.add(new KeywordScoreDTO(keyword, score))
        );
        return results;
    }

    @Override
    protected List<EventEntity> getEventsByRepository(Long id) {
        return eventRepo.findByEventTypeAndEmailIntegrationUserId(CATEGORY.getAction(), id);
    }

    @Override
    protected List<KeywordScoreDTO> keywordCalculation(EventEntity event) {
        List<KeywordScoreDTO> results = new ArrayList<>();
        List<CategoryEntity> categoryEntities = findCategoryEntity(event.getId());

        double score = calculateScore();
        List<String> keywords = createKeyword(categoryEntities);

        results.addAll(createKeywordScoreList(keywords, score));

        return results;
    }
}
