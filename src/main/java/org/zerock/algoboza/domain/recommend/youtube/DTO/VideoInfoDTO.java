package org.zerock.algoboza.domain.recommend.youtube.DTO;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record VideoInfoDTO(
        Meta meta,
        List<Data> data

) {
    public record Meta(
            @JsonProperty("search_keyword")
            List<String> searchKeyword,
            @JsonProperty("running_time")
            double runningTime
    ) {
    }

    public record Data(
            String id,
            String title,
            String duration,
            String url,
            String description,
            String channel,
            @JsonProperty("published_at")
            String publishedAt,
            String thumbnail
    ) {
    }
}
