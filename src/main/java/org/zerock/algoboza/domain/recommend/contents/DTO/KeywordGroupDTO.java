package org.zerock.algoboza.domain.recommend.contents.DTO;

import java.util.List;

public record KeywordGroupDTO(
        String keyword,
        List<String> options) {
}
