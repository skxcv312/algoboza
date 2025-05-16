package org.zerock.algoboza.domain.recommend.interestTracking.Service.Ecommerce;


import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.algoboza.domain.recommend.interestTracking.DTO.KeywordScoreDTO;
import org.zerock.algoboza.domain.recommend.interestTracking.Service.core.LogActionWeight;
import org.zerock.algoboza.domain.recommend.interestTracking.Service.core.WeightingInterest;
import org.zerock.algoboza.entity.logs.EventEntity;
import org.zerock.algoboza.entity.logs.search.SearchEntity;
import org.zerock.algoboza.repository.logs.EventRepo;
import org.zerock.algoboza.repository.logs.searchRepo.SearchRepo;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchInterestService extends WeightingInterest {
    private final SearchRepo searchRepo;
    private final EventRepo eventRepo;

    private final LogActionWeight SEARCH = LogActionWeight.SEARCH;

    private double calculateScore() {
        return SEARCH.getWeights();
    }

    private SearchEntity findCategoryEntity(Long eventId) {
        return searchRepo.findByEventId(eventId)
                .orElseThrow(() -> new RuntimeException("Search not found"));
    }

    private List<String> createKeyword(SearchEntity searchEntity) {
        List<String> keywords = new ArrayList<>();
        keywords.add(searchEntity.getSearchText());
        return keywords;
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
        return eventRepo.findByEventTypeAndEmailIntegrationUserId(SEARCH.getAction(), id);
    }

    @Override
    protected List<KeywordScoreDTO> keywordCalculation(EventEntity event) {
        List<KeywordScoreDTO> results = new ArrayList<>();
        SearchEntity searchEntity = findCategoryEntity(event.getId());
        double score = calculateScore();
        List<String> keywords = createKeyword(searchEntity);

        results.addAll(createKeywordScoreList(keywords, score));

        return results;
    }
}
