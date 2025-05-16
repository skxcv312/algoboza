package org.zerock.algoboza.domain.recommend.contents.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private int user_id;
    private Map<String, List<NaverResult>> naver_results;
    private Map<String, List<NaverPlace>> naver_places;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NaverResult {
        private String title;
        private String link;
        private String image;
        private String lprice;
        private String hprice;
        private String mallName;
        private String productId;
        private String productType;
        private String brand;
        private String maker;
        private String category1;
        private String category2;
        private String category3;
        private String category4;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NaverPlace {
        private String title;
        private String address;
        private String category;
        private String link;
    }
}
