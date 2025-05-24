package org.zerock.algoboza.domain.recommend.contents.service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
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
import org.zerock.algoboza.domain.recommend.interestTracking.Service.naver.PlaceInterestService;
import org.zerock.algoboza.entity.EmailIntegrationEntity;
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
@Transactional
public class RecommendService {

    private final InterestKeywordService interestKeywordService;
    private final KeywordScoreRedisRepo keywordScoreRedisRepo;
    private final KeywordTypeScoreRepo keywordTypeScoreRepo;
    private final RecommendResponseRedisRepo recommendResponseRedisRepo;
    private final PlaceInterestService placeInterestService;
    private final WebClient webClient;
    private final UserRepo userRepo;
    private final JsonUtils jsonUtils;

    @Value("${server-url}")
    private String SERVER_URL;
    int LIMIT_SIZE = 5;
    String SHOPPING = "shopping";

    /**
     * Redis에 키워드 점수를 저장
     */
    public void saveKeywordScoresToRedis(Long userId, List<KeywordTypeScoreDTO> keywordScoreDTOList) {
        log.debug("saveKeywordScoresToRedis{}", keywordScoreDTOList.toString());

        UserEntity userEntity = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("아이디가 없음"));

        // 충돌 방지: 이미 있으면 그대로 사용, 없으면 새로 저장
        KeywordScoreRedisEntity redisEntity = keywordScoreRedisRepo.findById(userId)
                .map(existing -> {
                    existing.setEventUpdateNum(0);
                    return existing;
                })
                .orElseGet(() ->
                        KeywordScoreRedisEntity.builder()
                                .user(userEntity)
                                .eventUpdateNum(0)
                                .build()
                );

        keywordScoreRedisRepo.save(redisEntity);
        keywordTypeScoreRepo.deleteByKeywordScoreRedisEntityId(redisEntity.getId());
        List<KeywordTypeScoreEntity> keywordTypeScoreEntityList = keywordScoreDTOList.stream()
                .map(v -> KeywordTypeScoreEntity.builder()
                        .keywordScoreRedisEntity(redisEntity)
                        .type(v.type())
                        .keyword(v.keyword())
                        .score(v.score())
                        .build())
                .toList();

        keywordTypeScoreRepo.saveAll(keywordTypeScoreEntityList);
    }

    /**
     * 추천 결과 저장
     */

    public void saveRecommendResponseToRedis(Long userId, UserResponse userResponse) {
        log.info("saveRecommendResponseToRedis {}", userResponse);

        String jsonResponse = jsonUtils.toJson(userResponse);
        UserEntity user = userRepo.findById(userId).orElseThrow(RuntimeException::new);

        // 기존 엔티티가 있는지 확인하고 업데이트하거나 새로 생성
        RecommendResponseRedisEntity entity = recommendResponseRedisRepo.findById(userId)
                .map(existing -> {
                    existing.setUserResponse(jsonResponse);
                    return existing;
                })
                .orElseGet(() -> RecommendResponseRedisEntity.builder()
                        .userResponse(jsonResponse)
                        .user(user)
                        .build());

        recommendResponseRedisRepo.save(entity);
    }

    /**
     * Redis에서 키워드 점수를 가져옴
     */
    public KeywordScoreRedisEntity fetchKeywordScoresFromRedis(Long userId) {
        return keywordScoreRedisRepo.findById(userId).orElse(null);
    }

    /**
     * Redis에서 userResponse 가져옴
     */
    public RecommendResponseRedisEntity fetchRecommendResponseFromRedis(Long userId) {
        return recommendResponseRedisRepo.findById(userId).orElse(null);
    }

    /**
     * Redis 데이터 유효성 검사
     */
    public boolean isRedisDataValid(KeywordScoreRedisEntity scores) {
        return scores != null && scores.getEventUpdateNum() < LIMIT_SIZE;
    }

    /**
     * Redis 엔티티를 DTO 리스트로 변환
     */
    public List<KeywordTypeScoreDTO> convertRedisDataToDTO(KeywordScoreRedisEntity scores) {
        List<KeywordTypeScoreEntity> keywordScoreRedisEntity = keywordTypeScoreRepo.findByKeywordScoreRedisEntity(
                scores);
        return keywordScoreRedisEntity.stream()
                .map(v -> KeywordTypeScoreDTO.builder()
                        .keyword(v.getKeyword())
                        .score(v.getScore())
                        .type(v.getType())
                        .build())
                .toList();
    }

    /**
     * WebClient POST 요청 공통 메서드
     */
    public <T, R> R createWebClientRequest(String uri, T body, Class<R> responseType) {
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
        return refreshUserRecommendation(user);
    }

    /**
     * 관심 점수 계산 + 추천 생성 + Redis 저장까지 한 번에 처리
     */
    private List<KeywordTypeScoreDTO> refreshUserRecommendation(UserEntity user) {

        recommendResponseRedisRepo.deleteByUserId(user.getId());
        List<KeywordTypeScoreDTO> interestScores = interestKeywordService.getInterestScore(user);

        saveKeywordScoresToRedis(user.getId(), interestScores);

        return interestScores;
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
    public UserResponse recommendContent(UserEntity user) {
        RecommendResponseRedisEntity recommendResponseRedisEntity = fetchRecommendResponseFromRedis(user.getId());
        if (recommendResponseRedisEntity == null) {
            List<KeywordTypeScoreDTO> interestScores = getKeywordTypeScore(user);
            List<TypeKeywordDTO> typeKeywordDTO = groupKeywordsByType(interestScores);
            UserResponse userResponse = createUserResponseFromInterest(user, typeKeywordDTO);

            saveRecommendResponseToRedis(user.getId(), userResponse);
            return userResponse;
        }
        String json = recommendResponseRedisEntity.getUserResponse();
        return jsonUtils.fromJson(json, UserResponse.class);
    }


    /**
     * 관심 키워드 기반 추천 생성
     */
    private UserResponse createUserResponseFromInterest(UserEntity user, List<TypeKeywordDTO> typeKeywordDTOList) {
        List<SearchKeywordDTO> searchKeywords = new ArrayList<>(typeKeywordDTOList.stream()
                .filter(typeKeywordDTO -> typeKeywordDTO.type().equals(SHOPPING))
                .flatMap(typeKeywordDTO -> identifyKeywords(typeKeywordDTO.type(), typeKeywordDTO.keywords()).stream()
                        .map(keyword -> new SearchKeywordDTO(keyword.keyword(), typeKeywordDTO.type(),
                                keyword.options())))
                .toList());

        // 장소는 따로 계산
        List<SearchKeywordDTO> searchKeywordDTOList = placeInterestService.getSearchKeywordDTO(user.getId());
        searchKeywords.addAll(searchKeywordDTOList);

        MetaData metaData = new MetaData("Seoul", user.getBirthDate(), LocalDateTime.now().toString(), "사용자가 작성한 노트");
        UserInterestDTO userInterest = new UserInterestDTO(Math.toIntExact(user.getId()), metaData, searchKeywords);
        log.info("userInterest {}", userInterest);

        UserResponse userResponse = createWebClientRequest(SERVER_URL + "/analyze", userInterest, UserResponse.class);
        userResponse = filterDuplicateTitles(userResponse);
        return userResponse;
    }

    /**
     * 필터 중복 타이틀
     */
    public UserResponse filterDuplicateTitles(UserResponse userResponse) {
        Function<String, String> cleanTitle = title -> title.replaceAll("<[^>]*>", "").trim();
        Set<String> globalTitleSet = new HashSet<>();

        // naver_results 중복 제거
        Map<String, List<UserResponse.NaverResult>> filteredResults =
                userResponse.getNaver_results().entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> new ArrayList<>(
                                        entry.getValue().stream()
                                                .collect(Collectors.toMap(
                                                        r -> cleanTitle.apply(r.getTitle()),
                                                        r -> r,
                                                        (r1, r2) -> r1
                                                ))
                                                .values()
                                )
                        ));

        // naver_places 중복 제거 (지역 구조 유지)
        Map<String, List<UserResponse.NaverPlace>> filteredPlaces =
                userResponse.getNaver_places().entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> entry.getValue().stream()
                                        .filter(place -> globalTitleSet.add(cleanTitle.apply(place.getTitle())))
                                        .toList()
                        ));

        return new UserResponse(userResponse.getUser_id(), filteredResults, filteredPlaces);
    }

    /**
     * 키워드를 WebClient로 분석하여 그룹화
     */
    public List<KeywordGroupDTO> identifyKeywords(String type, List<String> keywords) {
        return webClient.post()
                .uri(SERVER_URL + "/api/keyword/processing/" + type)
                .body(BodyInserters.fromValue(keywords))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<KeywordGroupDTO>>() {
                })
                .block();
    }
}
