package org.zerock.algoboza.domain.mypage.DTO;

import lombok.Builder;

@Builder
public record AlgorithmCartDTO(
        String keyword,
        int score
) {
}
