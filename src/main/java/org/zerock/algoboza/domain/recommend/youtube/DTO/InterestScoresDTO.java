package org.zerock.algoboza.domain.recommend.youtube.DTO;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.Map;

@Builder
public record InterestScoresDTO(
        @JsonProperty("interest_scores") Map<String, Integer> interestScores) {
    public Object exchangeToMono(Object o) {
        return null;
    }
}
