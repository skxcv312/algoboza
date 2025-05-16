package org.zerock.algoboza.domain.recommend.interestTracking;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.algoboza.domain.recommend.contents.DTO.KeywordTypeScoreDTO;
import org.zerock.algoboza.domain.recommend.interestTracking.DTO.KeywordScoreDTO;
import org.zerock.algoboza.domain.recommend.interestTracking.Service.Ecommerce.CartInterestService;
import org.zerock.algoboza.domain.recommend.interestTracking.Service.Ecommerce.CategoryInterestService;
import org.zerock.algoboza.domain.recommend.interestTracking.Service.Ecommerce.ProductInterestService;
import org.zerock.algoboza.domain.recommend.interestTracking.Service.Ecommerce.SearchInterestService;
import org.zerock.algoboza.domain.recommend.interestTracking.Service.naver.PlaceInterestService;
import org.zerock.algoboza.entity.EmailIntegrationEntity;
import org.zerock.algoboza.entity.UserEntity;
import org.zerock.algoboza.repository.EmailIntegrationRepo;
import org.zerock.algoboza.repository.logs.EventRepo;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterestKeywordService {
    private final ProductInterestService productInterestService;
    private final CategoryInterestService categoryInterestService;
    private final SearchInterestService searchInterestService;
    private final CartInterestService cartInterestService;
    private final EmailIntegrationRepo emailIntegrationRepo;
    private final PlaceInterestService placeInterestService;
    private final EventRepo eventRepo;

    String SHOPPING = "shopping";
    String PLACE = "place";

    int MAXIMUM_SIZE = 10;

    // 키워드 점수 계산 방식을 정의하는 메서드
    private double calculateAverageScore(List<KeywordScoreDTO> keywordScores) {
        log.info("calculateAverageScore running");
        return keywordScores.stream()
                .mapToDouble(KeywordScoreDTO::score)
                .sum();
    }

    public List<KeywordScoreDTO> mergeByKeyword(List<KeywordScoreDTO> originalList) {
        Map<String, Double> scoreMap = new HashMap<>();

        for (KeywordScoreDTO dto : originalList) {
            scoreMap.put(dto.keyword(), scoreMap.getOrDefault(dto.keyword(), 0.0) + dto.score());
        }

        return scoreMap.entrySet().stream()
                .map(entry -> new KeywordScoreDTO(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> Double.compare(b.score(), a.score())) // 내림차순 정렬
                .limit(MAXIMUM_SIZE) // 상위 2개만 추출
                .collect(Collectors.toList());
    }

    // 키워드 중복 제거 및 평균 점수 계산
    public List<KeywordTypeScoreDTO> mergeDuplicateKeywordScores(List<KeywordTypeScoreDTO> originalList) {
        // Map<"type|keyword", 총점>
        Map<String, Double> mergedMap = new HashMap<>();

        for (KeywordTypeScoreDTO dto : originalList) {
            String key = dto.type() + "|" + dto.keyword(); // 복합 키 생성
            mergedMap.put(key, mergedMap.getOrDefault(key, 0.0) + dto.score());
        }

        // 다시 DTO 리스트로 변환
        return mergedMap.entrySet().stream()
                .map(entry -> {
                    String[] parts = entry.getKey().split("\\|", 2);
                    String type = parts[0];
                    String keyword = parts[1];
                    return new KeywordTypeScoreDTO(keyword, type, entry.getValue());
                })
                .sorted(Comparator.comparingDouble(KeywordTypeScoreDTO::score).reversed()) // 내림차순 정렬
                .collect(Collectors.toList());
    }

    public List<EmailIntegrationEntity> getSameUserEntity(Long userId) {
        return emailIntegrationRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

    }

    // 관심 키워드 수집 및 중복 제거 메서드
    private List<KeywordTypeScoreDTO> collectShoppingInterestScores(Long id) {
// 비동기 호출
        CompletableFuture<List<KeywordScoreDTO>> productFuture =
                CompletableFuture.supplyAsync(() -> productInterestService.getInterest(id));

        CompletableFuture<List<KeywordScoreDTO>> categoryFuture =
                CompletableFuture.supplyAsync(() -> categoryInterestService.getInterest(id));

        CompletableFuture<List<KeywordScoreDTO>> searchFuture =
                CompletableFuture.supplyAsync(() -> searchInterestService.getInterest(id));

        CompletableFuture<List<KeywordScoreDTO>> cartFuture =
                CompletableFuture.supplyAsync(() -> cartInterestService.getInterest(id));

        // 모든 작업이 끝날 때까지 대기하고 결과 합치기
        List<KeywordScoreDTO> interestScores = new ArrayList<>();
        try {
            interestScores = CompletableFuture.allOf(
                    productFuture, categoryFuture, searchFuture, cartFuture
            ).thenApply(voided -> Stream.of(productFuture, categoryFuture, searchFuture, cartFuture)
                    .flatMap(future -> {
                        try {
                            return future.get().stream();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                            return Stream.empty();
                        }
                    })
                    .collect(Collectors.toList())
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("비동기 작업 실패", e); // 예외 감싸서 던짐
            // 필요 시 fallback 로직 추가

        }
        List<KeywordScoreDTO> topInterestScores = mergeByKeyword(interestScores);
        return aaa(topInterestScores, SHOPPING);
    }

    public List<KeywordTypeScoreDTO> aaa(List<KeywordScoreDTO> interestScores, String type) {
        return interestScores.stream()
                .map(v -> new KeywordTypeScoreDTO(v.keyword(), type, v.score()))
                .sorted(Comparator.comparingDouble(KeywordTypeScoreDTO::score).reversed())
                .toList();
    }


    public List<KeywordTypeScoreDTO> collectPlaceInterestScores(Long id) {
        List<KeywordScoreDTO> placeFuture = placeInterestService.getInterest(id);

        return aaa(placeFuture, PLACE);
    }


    public List<KeywordTypeScoreDTO> getInterestScore(UserEntity userEntity) {
        List<KeywordTypeScoreDTO> interestScores = new ArrayList<>();
        List<EmailIntegrationEntity> emailIntegrationEntityList = getSameUserEntity(userEntity.getId());

        for (EmailIntegrationEntity emailIntegrationEntity : emailIntegrationEntityList) {
            // 관심 키워드 수집 메서드 호출
            if (eventRepo.findByEmailIntegrationUserId(emailIntegrationEntity.getId()).isPresent()) {
                interestScores.addAll(
                        collectShoppingInterestScores(emailIntegrationEntity.getId()));

                interestScores.addAll(
                        collectPlaceInterestScores(emailIntegrationEntity.getId()));
            }

        }

        log.info("interestScores {}", interestScores);
        return mergeDuplicateKeywordScores(interestScores);

    }


}
