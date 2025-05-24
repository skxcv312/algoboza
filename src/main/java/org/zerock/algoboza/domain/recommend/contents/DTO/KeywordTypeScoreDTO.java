package org.zerock.algoboza.domain.recommend.contents.DTO;

import lombok.Builder;
import lombok.Getter;

@Builder
public record KeywordTypeScoreDTO(
        String keyword,
        String type,
        double score
) {
}
