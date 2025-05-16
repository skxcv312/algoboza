package org.zerock.algoboza.domain.recommend.contents.DTO;

import java.util.List;

public record UserInterestDTO(
        int user_id,
        MetaData meta_data,
        List<SearchKeywordDTO> interest_scores
) {
    public record SearchKeywordDTO(
            String keyword,
            String type,
            List<String> options
    ) {
    }

    public record MetaData(
            String location,
            String birth_date,
            String timestamp,
            String note
    ) {
    }

}
