package org.zerock.algoboza.domain.youtube.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
public record InterestScoresDTO(
        List<keyword> interest_scores
) {
    @Builder
    public record keyword(
            String keyword,
            int scores
    ) {
    }
}
