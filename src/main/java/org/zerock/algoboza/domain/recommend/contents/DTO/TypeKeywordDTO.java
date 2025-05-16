package org.zerock.algoboza.domain.recommend.contents.DTO;

import java.util.List;

public record TypeKeywordDTO(
        String type,
        List<String> keywords
) {
}
