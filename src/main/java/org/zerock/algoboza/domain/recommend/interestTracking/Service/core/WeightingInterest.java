package org.zerock.algoboza.domain.recommend.interestTracking.Service.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.algoboza.domain.recommend.interestTracking.DTO.KeywordScoreDTO;
import org.zerock.algoboza.entity.UserEntity;
import org.zerock.algoboza.entity.logs.EventEntity;
import org.zerock.algoboza.entity.logs.ViewEntity;
import org.zerock.algoboza.repository.logs.ViewRepo;

@Log4j2
@Transactional(readOnly = true)
public abstract class WeightingInterest {

    // ViewRepo를 자동 주입하여 사용
    @Autowired
    private ViewRepo viewRepo;

    /**
     * 이벤트 타입별, 시단대별, 원하는 엔티티를 분류하기 위한 추상 메서드
     */
    protected abstract List<EventEntity> getEventsByRepository(Long id);

    /**
     * 키워드를 추출과 점수 설정하기 위한 추상 메서드
     */
    protected abstract List<KeywordScoreDTO> keywordCalculation(EventEntity event);

    protected List<KeywordScoreDTO> Scorecard(List<KeywordScoreDTO> keywordScoreDTOList, int maximumSizeInterest) {
        return keywordScoreDTOList.stream()
                .sorted(Comparator.comparingDouble(KeywordScoreDTO::score).reversed())
                .toList();
    }

    /**
     * 타입별 관심키워드 호출함 사용자 관심도 점수를 계산하여 반환하는 메서드
     */
    public List<KeywordScoreDTO> getInterest(Long id) {
        // 이벤트 타입별 엔티티 분류
        List<EventEntity> eventEntityList = getEventsByRepository(id);

        // 초기 키워드 점수 리스트 설정
        List<KeywordScoreDTO> keywordScoreDTOList = setKeywordScore(eventEntityList);

        log.info("keywordScoreDTOList{}", keywordScoreDTOList);

        return keywordScoreDTOList;
    }

    /**
     * 키워드와 가중치를 이용하여 RowKeywordScore 리스트를 생성하는 메서드
     *
     * @param keywordList
     * @param weightingViewTime
     * @return
     */
    protected List<KeywordScoreDTO> createKeywordScore(List<KeywordScoreDTO> keywordList, double weightingViewTime) {
        return keywordList.stream()
                .map(keywordScore -> {
                    String keyword = keywordScore.keyword();
                    double adjustedScore = keywordScore.score() * weightingViewTime;
                    return new KeywordScoreDTO(keyword, adjustedScore);
                })
                .toList();
    }

    /**
     * // 이벤트 엔티티 리스트를 기반으로 RowKeywordScore 리스트를 설정하는 메서드
     *
     * @param eventEntitieList
     * @return
     */
    protected List<KeywordScoreDTO> setKeywordScore(List<EventEntity> eventEntitieList) {
        List<KeywordScoreDTO> keywordScoreList = new ArrayList<>();
        for (EventEntity event : eventEntitieList) {
            // 가중치 계산
            double weightingViewTime = calculateWeightingViewTime(event);
            if (weightingViewTime == 0) {
                continue;  // 가중치가 0이면 건너뜀
            }
            // 키워드 추출 후 점수 부여
            List<KeywordScoreDTO> keywordList = keywordCalculation(event);

            // RowKeywordScore 생성 및 추가
            List<KeywordScoreDTO> newKeywordScores = createKeywordScore(keywordList, weightingViewTime);
            keywordScoreList.addAll(newKeywordScores);
        }
        return keywordScoreList;
    }

    /**
     * 사용자 유효성 검사 메서드
     *
     * @param userEntity
     */
    private void validateUser(UserEntity userEntity) {
        if (userEntity == null) {
            throw new IllegalArgumentException("UserEntity must not be null");
        }
    }

    /**
     * 이벤트 ID를 기반으로 ViewEntity를 조회하는 메서드
     *
     * @param eventId
     * @return
     */

    public ViewEntity viewFindByEventId(long eventId) {
        return viewRepo.findByEventId(eventId)
                .orElseThrow(() -> new IllegalArgumentException("View not found for event ID: " + eventId));
    }

    /**
     * 이벤트를 기반으로 뷰 시간 가중치를 계산하는 메서드
     */
    public double calculateWeightingViewTime(EventEntity eventEntity) {
        // ViewEntity 조회
        ViewEntity viewEntity = viewFindByEventId(eventEntity.getId());

        // 체류 시간 가중치와 스크롤 가중치 계산
        double dwellTimeScore = calculateDwellTimeScore(viewEntity);
        double totalScrollScore = calculateTotalScrollScore(viewEntity);

        // 활동 없는 경우 0 반환
        if (isInactive(dwellTimeScore, totalScrollScore)) {
            return 0;
        }
        // 두 점수의 합 반환
        return dwellTimeScore + totalScrollScore;
    }

    /**
     * 활동 유무를 판단하는 메서드
     */
    private boolean isInactive(double dwellTimeScore, double totalScrollScore) {
        return dwellTimeScore == 0 || totalScrollScore == 0;
    }

    /**
     * 체류 시간 가중치를 계산하는 메서드
     *
     * @param viewEntity
     * @return
     */
    private double calculateDwellTimeScore(ViewEntity viewEntity) {
        return viewEntity.getDwellTime() * LogActionWeight.DWELL_TIME.getWeights();
    }


    /**
     * 스크롤 가중치를 계산하는 메서드
     *
     * @param viewEntity
     * @return
     */
    private double calculateTotalScrollScore(ViewEntity viewEntity) {
        return viewEntity.getTotalScroll() * LogActionWeight.TOTAL_SCROLL.getWeights();
    }
}
