package org.zerock.algoboza.domain.recommend.contents.DTO;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInterestDTO {
    private int user_id;
    private MetaData meta_data;
    private List<SearchKeywordDTO> interest_scores;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SearchKeywordDTO {
        private String keyword;
        private String type;
        private List<String> options;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MetaData {
        private String location;
        private String birth_date;
        private String timestamp;
        private String note;
    }
}
