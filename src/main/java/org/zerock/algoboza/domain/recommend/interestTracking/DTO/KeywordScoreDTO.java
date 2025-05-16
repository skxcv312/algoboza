package org.zerock.algoboza.domain.recommend.interestTracking.DTO;

import lombok.Builder;

@Builder
public record KeywordScoreDTO(
        String keyword,        // 키워드
        double score  // 가중치 값
) {
}
