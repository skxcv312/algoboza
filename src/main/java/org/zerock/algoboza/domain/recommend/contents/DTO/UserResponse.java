package org.zerock.algoboza.domain.recommend.contents.DTO;

import java.util.List;
import java.util.Map;

public record UserResponse(
        int user_id,
        Map<String, List<NaverResult>> naver_results,
        Map<String, List<NaverPlace>> naver_places
) {
    public record NaverResult(
            String title,
            String link,
            String image,
            String lprice,
            String hprice,
            String mallName,
            String productId,
            String productType,
            String brand,
            String maker,
            String category1,
            String category2,
            String category3,
            String category4
    ) {
    }

    public record NaverPlace(
            String title,
            String address,
            String category,
            String link
    ) {
    }
}
