package org.zerock.algoboza.domain.youtube.DTO;


import lombok.Builder;

import java.util.List;

@Builder
public record InterestScoreDTO(
        List<keyword> interest_scores
) {
    record keyword(
            String keyword,
            int scores
    ) {
    }
}
