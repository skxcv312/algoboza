package org.zerock.algoboza.domain.recommend.contents.DTO;

import lombok.Builder;

@Builder
public record KeywordTypeScoreDTO(
        String keyword,
        String type,
        double score
) {
}
