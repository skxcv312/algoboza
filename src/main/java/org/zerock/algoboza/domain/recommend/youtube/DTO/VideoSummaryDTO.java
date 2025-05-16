package org.zerock.algoboza.domain.recommend.youtube.DTO;

public record VideoSummaryDTO(
        Meta meta,
        Data data
) {
    public record Meta(
            String video_id,
            String running_time
    ) {
    }

    public record Data(
            String description
    ) {
    }
}
