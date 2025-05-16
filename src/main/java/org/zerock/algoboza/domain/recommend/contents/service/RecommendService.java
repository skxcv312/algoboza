package org.zerock.algoboza.domain.recommend.contents.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.zerock.algoboza.domain.recommend.contents.DTO.*;
import org.zerock.algoboza.domain.recommend.contents.DTO.UserInterestDTO.MetaData;
import org.zerock.algoboza.domain.recommend.contents.DTO.UserInterestDTO.SearchKeywordDTO;
import org.zerock.algoboza.domain.recommend.interestTracking.DTO.KeywordScoreDTO;
import org.zerock.algoboza.domain.recommend.interestTracking.InterestKeywordService;
import org.zerock.algoboza.entity.UserEntity;
import org.zerock.algoboza.entity.redis.KeywordScoreRedisEntity;
import org.zerock.algoboza.repository.redis.RedisRepo;

@Log4j2
@Service
@RequiredArgsConstructor
public class RecommendService {

    private final InterestKeywordService interestKeywordService;
    private final RedisRepo redisRepo;
    private final WebClient webClient;

    @Value("${server-url}")
    private String SERVER_URL;
    private static final int LIMIT_SIZE = 30;
    private static final String SHOPPING = "shopping";

    /**
     * Redis에 키워드 점수를 저장
     */
    private void saveKeywordScoresToRedis(Long userId, List<KeywordTypeScoreDTO> keywordScoreDTOList) {
        log.debug("saveKeywordScoresToRedis{}", keywordScoreDTOList.toString());
        KeywordScoreRedisEntity redisEntity = KeywordScoreRedisEntity.builder()
                .id(userId)
                .scoreList(keywordScoreDTOList)
                .eventUpdateNum(0)
                .build();
        redisRepo.save(redisEntity);
    }

    /**
     * Redis에서 키워드 점수를 가져옴
     */
    private KeywordScoreRedisEntity fetchKeywordScoresFromRedis(Long userId) {
        return redisRepo.findById(userId).orElse(null);
    }

    /**
     * Redis 데이터 유효성 검사
     */
    private boolean isRedisDataValid(KeywordScoreRedisEntity scores) {
        return scores != null && scores.getScoreList() != null && scores.getEventUpdateNum() < LIMIT_SIZE;
    }

    /**
     * Redis 엔티티를 DTO 리스트로 변환
     */
    private List<KeywordTypeScoreDTO> convertRedisDataToDTO(KeywordScoreRedisEntity scores) {
        return scores.getScoreList().stream()
                .map(v -> KeywordTypeScoreDTO.builder()
                        .keyword(v.keyword())
                        .score(v.score())
                        .type(v.type())
                        .build())
                .toList();
    }

    /**
     * WebClient POST 요청 공통 메서드
     */
    private <T, R> R createWebClientRequest(String uri, T body, Class<R> responseType) {
        return webClient.post()
                .uri(uri)
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    /**
     * 사용자의 Ecomus 관심 점수를 가져오는 메서드
     */
    public List<KeywordTypeScoreDTO> getKeywordTypeScore(UserEntity user) {
        KeywordScoreRedisEntity scores = fetchKeywordScoresFromRedis(user.getId());
        if (isRedisDataValid(scores)) {
            log.debug("Redis count num{}", scores.getEventUpdateNum());
            return convertRedisDataToDTO(scores);
        }

        List<KeywordScoreDTO> shoppingScores = interestKeywordService.getInterestScore(user);
        List<KeywordTypeScoreDTO> keywordScoreDTOs = mapToTypeKeyword(shoppingScores, SHOPPING);

        saveKeywordScoresToRedis(user.getId(), keywordScoreDTOs);
        return keywordScoreDTOs;
    }

    /**
     * 키워드 점수를 타입별로 매핑
     */
    public List<KeywordTypeScoreDTO> mapToTypeKeyword(List<KeywordScoreDTO> scores, String type) {
        return scores.stream()
                .map(score -> KeywordTypeScoreDTO.builder()
                        .type(type)
                        .keyword(score.keyword())
                        .score(score.score())
                        .build())
                .toList();
    }

    /**
     * 키워드를 유형별로 그룹화
     */
    public List<TypeKeywordDTO> groupKeywordsByType(List<KeywordTypeScoreDTO> keywords) {
        return keywords.stream()
                .collect(Collectors.groupingBy(
                        KeywordTypeScoreDTO::type,
                        Collectors.mapping(KeywordTypeScoreDTO::keyword, Collectors.toList())
                ))
                .entrySet().stream()
                .map(entry -> new TypeKeywordDTO(entry.getKey(), entry.getValue()))
                .toList();
    }

    /**
     * 추천 콘텐츠 생성
     */
    public UserResponse recommendContent(UserEntity user, List<TypeKeywordDTO> typeKeywordDTOList) {
        List<SearchKeywordDTO> searchKeywords = typeKeywordDTOList.stream()
                .flatMap(typeKeywordDTO -> identifyKeywords(typeKeywordDTO.keywords()).stream()
                        .map(keyword -> new SearchKeywordDTO(keyword.keyword(), typeKeywordDTO.type(),
                                keyword.options())))
                .toList();

        MetaData metaData = new MetaData("Seoul", "2001-02-25", "2025-04-10T12:00:00Z", "사용자가 작성한 노트");
        UserInterestDTO userInterest = new UserInterestDTO(Math.toIntExact(user.getId()), metaData, searchKeywords);

        return createWebClientRequest(SERVER_URL + "/analyze", userInterest, UserResponse.class);
    }

    /**
     * 키워드를 WebClient로 분석하여 그룹화
     */
    private List<KeywordGroupDTO> identifyKeywords(List<String> keywords) {
        return webClient.post()
                .uri(SERVER_URL + "/api/keyword/processing")
                .body(BodyInserters.fromValue(keywords))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<KeywordGroupDTO>>() {
                })
                .block();
    }
}
