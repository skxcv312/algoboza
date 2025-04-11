package org.zerock.algoboza.domain.youtube.DTO;


import lombok.Builder;

@Builder
public record VideoSummaryDTO(
        String id,
        String title,
        String duration,
        String url,
        String description,
        String channel,
        String publishedAt,
        String thumbnail
) {
}
