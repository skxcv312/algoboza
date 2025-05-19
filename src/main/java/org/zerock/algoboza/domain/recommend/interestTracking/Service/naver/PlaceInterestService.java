package org.zerock.algoboza.domain.recommend.interestTracking.Service.naver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties.Streams;
import org.springframework.stereotype.Service;
import org.zerock.algoboza.domain.recommend.contents.DTO.UserInterestDTO.SearchKeywordDTO;
import org.zerock.algoboza.domain.recommend.interestTracking.DTO.KeywordScoreDTO;
import org.zerock.algoboza.domain.recommend.interestTracking.Service.core.LogActionWeight;
import org.zerock.algoboza.domain.recommend.interestTracking.Service.core.WeightingInterest;
import org.zerock.algoboza.entity.EmailIntegrationEntity;
import org.zerock.algoboza.entity.logs.EventEntity;
import org.zerock.algoboza.entity.logs.place.PlaceCategoryEntity;
import org.zerock.algoboza.entity.logs.place.PlaceDetailEntity;
import org.zerock.algoboza.entity.logs.place.PlaceEntity;
import org.zerock.algoboza.repository.EmailIntegrationRepo;
import org.zerock.algoboza.repository.logs.EventRepo;
import org.zerock.algoboza.repository.logs.PlaceRepo.PlaceCategoryRepo;
import org.zerock.algoboza.repository.logs.PlaceRepo.PlaceDetailRepo;
import org.zerock.algoboza.repository.logs.PlaceRepo.PlaceRepo;


@Service
@RequiredArgsConstructor
@Log4j2
public class PlaceInterestService {
    private final EventRepo eventRepo;
    private final PlaceRepo placeRepo;
    private final PlaceDetailRepo placeDetailRepo;
    private final PlaceCategoryRepo placeCategoryRepo;
    private final EmailIntegrationRepo emailIntegrationRepo;

    private final LogActionWeight PLACE = LogActionWeight.PLACE;
    private final LogActionWeight PLACE_ADDRESS = LogActionWeight.PLACE_ADDRESS;
    private final LogActionWeight PLACE_CATEGORY = LogActionWeight.PLACE_CATEGORY;

    int LIMIT_SIZE = 2;


    private List<KeywordScoreDTO> placeEntity_calculateScore(PlaceEntity placeEntity) {
        List<String> searchList = List.of(placeEntity.getSearchText().split("\\s+"));

        return searchList.stream()
                .map(v -> new KeywordScoreDTO(v, PLACE.getWeights()))
                .toList();
    }

    private KeywordScoreDTO placeDetail_calculateScore(PlaceDetailEntity placeDetail) {
        List<String> addressList = List.of(placeDetail.getAddress().split("\\s+"));
        String DetailedRegion = addressList.get(2);
        return new KeywordScoreDTO(DetailedRegion, PLACE_ADDRESS.getWeights());
    }

    private List<KeywordScoreDTO> placeCategories_calculateScore(List<PlaceCategoryEntity> placeCategories) {
        return placeCategories.stream().map(v -> new KeywordScoreDTO(v.getCategory(), PLACE_CATEGORY.getWeights()))
                .toList();
    }

    protected List<EventEntity> getEventsByRepository(Long id) {
        return eventRepo.findTop20ByEventTypeAndEmailIntegrationUserIdOrderByCreatedAtDesc(PLACE.getAction(), id);
    }

    public List<KeywordScoreDTO> mergeByKeyword(List<KeywordScoreDTO> originalList, int limitSize) {
        Map<String, Double> scoreMap = new HashMap<>();

        for (KeywordScoreDTO dto : originalList) {
            scoreMap.put(dto.keyword(), scoreMap.getOrDefault(dto.keyword(), 0.0) + dto.score());
        }

        return scoreMap.entrySet().stream()
                .map(entry -> new KeywordScoreDTO(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> Double.compare(b.score(), a.score())) // 내림차순 정렬
                .limit(limitSize) // 상위 2개만 추출
                .collect(Collectors.toList());
    }

    public List<KeywordScoreDTO> getInterest(Long id) {
        // 이벤트 타입별 엔티티 분류
        List<EventEntity> eventEntityList = getEventsByRepository(id);

        List<KeywordScoreDTO> placeResults = new ArrayList<>();
        List<KeywordScoreDTO> addressResults = new ArrayList<>();
        List<KeywordScoreDTO> categoryResults = new ArrayList<>();

        for (EventEntity eventEntity : eventEntityList) {
            // place entity 추출
            PlaceEntity placeEntity = placeRepo.findByEventId(eventEntity.getId());
            placeResults.addAll(placeEntity_calculateScore(placeEntity));

            // place detail entity 추출
            PlaceDetailEntity placeDetail = placeDetailRepo.findByPlaceId(placeEntity.getId());
            addressResults.add(placeDetail_calculateScore(placeDetail));

            // place category 추출
            List<PlaceCategoryEntity> placeCategories = placeCategoryRepo.findByPlaceDetailId(placeDetail.getId());
            categoryResults.addAll(placeCategories_calculateScore(placeCategories));

        }

        placeResults = mergeByKeyword(placeResults, 2);
        addressResults = mergeByKeyword(addressResults, 2);
        categoryResults = mergeByKeyword(categoryResults, 4);

        List<KeywordScoreDTO> mergedAll = new ArrayList<>();
        mergedAll.addAll(placeResults);
        mergedAll.addAll(addressResults);
        mergedAll.addAll(categoryResults);

        return mergedAll;
    }

    record PlaceCategory(
            String searchText,
            List<String> place,
            List<String> category
    ) {
    }

    public List<SearchKeywordDTO> getSearchKeywordDTO(Long userId) {
        List<EmailIntegrationEntity> emailIntegrationEntityList = emailIntegrationRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("email is null"));

        // Group event IDs by searchText from their associated PlaceEntity
        Map<String, List<Long>> searchTextToEventIds = new HashMap<>();
        for (EmailIntegrationEntity emailIntegrationEntity : emailIntegrationEntityList) {
            List<EventEntity> eventEntityList = getEventsByRepository(emailIntegrationEntity.getId());
            log.info("EventEntity count: {}", eventEntityList.size());

            for (EventEntity eventEntity : eventEntityList) {
                PlaceEntity placeEntity = placeRepo.findByEventId(eventEntity.getId());

                if (placeEntity == null) {
                    log.warn("PlaceEntity not found for eventId: {}", eventEntity.getId());
                    continue;
                }

                String searchText = placeEntity.getSearchText();
                if (searchText == null || searchText.isBlank()) {
                    log.warn("Empty searchText for placeEntity: {}", placeEntity.getId());
                    continue;
                }

                searchTextToEventIds
                        .computeIfAbsent(searchText, k -> new ArrayList<>())
                        .add(eventEntity.getId());
            }
            log.info("Grouped event IDs by searchText: {}", searchTextToEventIds);
        }

        List<PlaceCategory> placeCategoryList = new ArrayList<>();

        for (Map.Entry<String, List<Long>> entry : searchTextToEventIds.entrySet()) {
            String searchText = entry.getKey();
            List<Long> eventIds = entry.getValue();

            List<String> places = new ArrayList<>();
            List<String> categories = new ArrayList<>();

            for (Long eventId : eventIds) {
                PlaceEntity placeEntity = placeRepo.findByEventId(eventId);
                PlaceDetailEntity placeDetail = placeDetailRepo.findByPlaceId(placeEntity.getId());
                places.add(placeDetail.getAddress().split("\\s+")[1]);

                List<PlaceCategoryEntity> placeCategoryEntities = placeCategoryRepo.findByPlaceDetailId(
                        placeDetail.getId());
                categories.addAll(
                        placeCategoryEntities.stream().map(PlaceCategoryEntity::getCategory).toList()
                );
            }

            placeCategoryList.add(new PlaceCategory(
                    searchText,
                    new ArrayList<>(new HashSet<>(places)),
                    new ArrayList<>(new HashSet<>(categories))
            ));
        }

        log.info("Built PlaceCategory list: {}", placeCategoryList);

        List<SearchKeywordDTO> searchKeywordDTOList = new ArrayList<>();

        for (PlaceCategory placeCategory : placeCategoryList) {
            SearchKeywordDTO searchKeywordDTO = SearchKeywordDTO.builder()
                    .type("place")
                    .options(placeCategory.category())
                    .keyword(placeCategory.place().getFirst())
                    .build();
            searchKeywordDTOList.add(searchKeywordDTO);
        }

        log.info("Built SearchKeywordDTO list: {}", searchKeywordDTOList);

        return searchKeywordDTOList;
    }
}
