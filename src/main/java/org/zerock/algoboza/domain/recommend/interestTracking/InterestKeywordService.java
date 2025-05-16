package org.zerock.algoboza.domain.recommend.interestTracking;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.algoboza.domain.recommend.interestTracking.DTO.KeywordScoreDTO;
import org.zerock.algoboza.domain.recommend.interestTracking.Service.Ecommerce.CartInterestService;
import org.zerock.algoboza.domain.recommend.interestTracking.Service.Ecommerce.CategoryInterestService;
import org.zerock.algoboza.domain.recommend.interestTracking.Service.Ecommerce.ProductInterestService;
import org.zerock.algoboza.domain.recommend.interestTracking.Service.Ecommerce.SearchInterestService;
import org.zerock.algoboza.entity.EmailIntegrationEntity;
import org.zerock.algoboza.entity.UserEntity;
import org.zerock.algoboza.global.JsonUtils;
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
    private final EventRepo eventRepo;

    int MAXIMUM_SIZE_PER_INTEREST = 6;

    // 키워드 점수 계산 방식을 정의하는 메서드
    private double calculateAverageScore(List<KeywordScoreDTO> keywordScores) {
        log.info("calculateAverageScore running");
        return keywordScores.stream()
                .mapToDouble(KeywordScoreDTO::score)
                .sum();
    }

    // 키워드 중복 제거 및 평균 점수 계산
    protected List<KeywordScoreDTO> deduplicationKeyword(List<KeywordScoreDTO> keywordScoreDTOList) {
        log.info("deduplicationKeyword running");
        return keywordScoreDTOList.stream()
                .collect(Collectors.groupingBy(KeywordScoreDTO::keyword))
                .entrySet().stream()
                .map(entry -> new KeywordScoreDTO(entry.getKey(), calculateAverageScore(entry.getValue())))
                .sorted(Comparator.comparingDouble(KeywordScoreDTO::score).reversed()) // 점수 내림차순 정렬
                .toList();
    }

    public List<EmailIntegrationEntity> getSameUserEntity(Long userId) {
        return emailIntegrationRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

    }

    // 관심 키워드 수집 및 중복 제거 메서드
    private List<KeywordScoreDTO> collectInterestScores(Long id, int maxSize) {
        List<KeywordScoreDTO> interestScores = new ArrayList<>();

        interestScores.addAll(productInterestService.getInterest(id, maxSize));
        interestScores.addAll(categoryInterestService.getInterest(id, maxSize));
        interestScores.addAll(searchInterestService.getInterest(id, maxSize));
        interestScores.addAll(cartInterestService.getInterest(id, maxSize));

        // 중복 제거 및 계산
        return interestScores;
    }


    public List<KeywordScoreDTO> getInterestScore(UserEntity userEntity) {
        List<KeywordScoreDTO> interestScores = new ArrayList<>();
        List<EmailIntegrationEntity> emailIntegrationEntityList = getSameUserEntity(userEntity.getId());

        for (EmailIntegrationEntity emailIntegrationEntity : emailIntegrationEntityList) {
            // 관심 키워드 수집 메서드 호출
            if (eventRepo.findByEmailIntegrationUserId(emailIntegrationEntity.getId()).isPresent()) {

                interestScores.addAll(collectInterestScores(emailIntegrationEntity.getId(), MAXIMUM_SIZE_PER_INTEREST));
            }

        }

        return deduplicationKeyword(interestScores);
    }

}
