package org.zerock.algoboza.domain.recommend.contents.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.algoboza.domain.recommend.contents.DTO.KeywordTypeScoreDTO;
import org.zerock.algoboza.domain.recommend.contents.DTO.UserResponse;
import org.zerock.algoboza.domain.recommend.contents.DTO.UserResponse.NaverPlace;
import org.zerock.algoboza.domain.recommend.contents.DTO.UserResponse.NaverResult;
import org.zerock.algoboza.entity.UserEntity;
import org.zerock.algoboza.entity.redis.KeywordScoreRedisEntity;
import org.zerock.algoboza.entity.redis.KeywordTypeScoreEntity;
import org.zerock.algoboza.entity.redis.RecommendResponseRedisEntity;
import org.zerock.algoboza.global.JsonUtils;
import org.zerock.algoboza.repository.UserRepo;
import org.zerock.algoboza.repository.redis.KeywordScoreRedisRepo;
import org.zerock.algoboza.repository.redis.KeywordTypeScoreRepo;
import org.zerock.algoboza.repository.redis.RecommendResponseRedisRepo;

@Log4j2
@Service
@RequiredArgsConstructor
public class SomeThingElse {
    private final KeywordScoreRedisRepo keywordScoreRedisRepo;
    private final KeywordTypeScoreRepo keywordTypeScoreRepo;
    private final RecommendService recommendService;
    private final RecommendResponseRedisRepo recommendResponseRedisRepo;
    private final UserRepo userRepo;
    private final JsonUtils jsonUtils;

    public static class TopKeyword {
        public List<String> top3PlaceKeywords = new ArrayList<>();
        public List<String> top3ShoppingKeywords = new ArrayList<>();
    }

    public TopKeyword extractingTopKeywords(Long id) {
        KeywordScoreRedisEntity keywordScoreRedisEntity = keywordScoreRedisRepo.findById(id).orElse(null);
        if (keywordScoreRedisEntity == null) {
            return null;
        }
        List<KeywordTypeScoreEntity> keywordTypeScoreEntityList = keywordTypeScoreRepo.findByKeywordScoreRedisEntity(
                keywordScoreRedisEntity);

        List<KeywordTypeScoreDTO> keywordTypeScoreList = keywordTypeScoreEntityList.stream()
                .map(v -> KeywordTypeScoreDTO.builder()
                        .keyword(v.getKeyword())
                        .score(v.getScore())
                        .type(v.getType())
                        .build())
                .toList();
        return getTopKeyword(keywordTypeScoreList);
    }

    private TopKeyword getTopKeyword(List<KeywordTypeScoreDTO> keywordTypeScoreList) {
        TopKeyword myKeyword = new TopKeyword();
        for (KeywordTypeScoreDTO keywordTypeScoreDTO : keywordTypeScoreList) {
            if (keywordTypeScoreDTO.type().equals("place") && myKeyword.top3PlaceKeywords.size() < 3) {
                myKeyword.top3PlaceKeywords.add(keywordTypeScoreDTO.keyword());
            } else if (keywordTypeScoreDTO.type().equals("shopping") && myKeyword.top3ShoppingKeywords.size() < 3) {
                myKeyword.top3ShoppingKeywords.add(keywordTypeScoreDTO.keyword());
            }
        }
        return myKeyword;
    }

    public UserResponse getOtherResponse(UserEntity userEntity) {
        List<Long> userIdList = userRepo.findAllUserIds(userEntity.getId());
        List<KeywordTypeScoreDTO> keywordTypeScoreList = recommendService.getKeywordTypeScore(userEntity);
        log.info("keywordTypeScoreList {}", keywordTypeScoreList);

        TopKeyword myTopKeyword = getTopKeyword(keywordTypeScoreList);
        log.info("Top 3 Keywords: {} {}", myTopKeyword.top3ShoppingKeywords, myTopKeyword.top3PlaceKeywords);

        UserResponse otherResponse = new UserResponse();
        for (Long id : userIdList) {
            TopKeyword otherTopKeyword = extractingTopKeywords(id);
            if (otherTopKeyword == null) {
                continue;
            }

            long shoppingOverlap = myTopKeyword.top3ShoppingKeywords.stream()
                    .filter(otherTopKeyword.top3ShoppingKeywords::contains)
                    .count();
            log.info("shoppingOverlap {}", shoppingOverlap);

            long placeOverlap = myTopKeyword.top3PlaceKeywords.stream()
                    .filter(otherTopKeyword.top3PlaceKeywords::contains)
                    .count();
            log.info("placeOverlap {}", placeOverlap);

            if (shoppingOverlap >= 2 || placeOverlap >= 2) {
                log.info("User top keyword {}", otherTopKeyword);
                RecommendResponseRedisEntity recommendResponseRedisEntity = recommendResponseRedisRepo.findById(id)
                        .orElse(null);
                if (recommendResponseRedisEntity == null) {
                    continue;
                }
                String json = recommendResponseRedisEntity.getUserResponse();
                UserResponse userResponse = jsonUtils.fromJson(json, UserResponse.class);
                log.info("userResponse {}", userResponse);
                if (shoppingOverlap >= 2) {
                    otherResponse.setNaver_results(userResponse.getNaver_results());
                }
                if (placeOverlap >= 2) {
                    otherResponse.setNaver_places(userResponse.getNaver_places());
                }
            }
            if (otherResponse.getNaver_results() != null && otherResponse.getNaver_places() != null) {
                return otherResponse;
            }
        }

        return otherResponse;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record someThing(
            String title,
            String type,
            String link,
            String image,
            String lprice,
            String adrress,
            String category,
            Double lat,
            Double lng
    ) {
    }

    public Map<String, List<someThing>> adepter(UserResponse otherResponse) {
        Map<String, List<NaverPlace>> naverPlaces = otherResponse.getNaver_places();
        Map<String, List<NaverResult>> naverResults = otherResponse.getNaver_results();
        Map<String, List<someThing>> result = new HashMap<>();

        // Convert NaverResult to someThing with type "shopping"
        if (naverResults != null && !naverResults.isEmpty()) {
            for (Map.Entry<String, List<NaverResult>> entry : naverResults.entrySet()) {
                if (entry.getValue() == null || entry.getValue().isEmpty()) {
                    continue;
                }
                List<someThing> list = entry.getValue().stream()
                        .map(nr -> new someThing(nr.getTitle(), "shopping", nr.getLink(), nr.getImage(), nr.getLprice(),
                                null, null, null, null))
                        .toList();
                result.put(entry.getKey(), list);
            }
        }

        // Convert NaverPlace to someThing with type "place"
        if (naverPlaces != null && !naverPlaces.isEmpty()) {
            for (Map.Entry<String, List<NaverPlace>> entry : naverPlaces.entrySet()) {
                if (entry.getValue() == null || entry.getValue().isEmpty()) {
                    continue;
                }
                List<someThing> list = entry.getValue().stream()
                        .map(np -> new someThing(
                                np.getTitle(),
                                "place",
                                np.getLink(),
                                null,
                                null,
                                np.getAddress(),
                                np.getCategory(),
                                np.getLat(),
                                np.getLng()
                        ))
                        .toList();
                result.put(entry.getKey(), list);
            }
        }
        return result;
    }
}
