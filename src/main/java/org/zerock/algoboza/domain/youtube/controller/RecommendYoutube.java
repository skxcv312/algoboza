package org.zerock.algoboza.domain.youtube.controller;


import java.util.HashMap;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import org.zerock.algoboza.domain.auth.service.AuthService;
import org.zerock.algoboza.domain.youtube.DTO.InterestScoresDTO;
import org.zerock.algoboza.domain.youtube.service.RecommendYoutubeService;
import org.zerock.algoboza.entity.UserEntity;
import org.zerock.algoboza.global.JsonUtils;
import org.zerock.algoboza.global.Response;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/recommend/youtube")
public class RecommendYoutube {
    private final RecommendYoutubeService recommendYoutubeService;
    private final AuthService authService;
    private final JsonUtils jsonUtils;

    @Value("${fast.url}")
    private String summaryURL;

    @Value("${fast.api_key}")
    private String apiKey;

    @GetMapping()
    public Response<?> recommendYoutube() {
        UserEntity user = authService.getUserContext();

        // 유저 관심도 얻기
        InterestScoresDTO interestScores = recommendYoutubeService.getInterestScores(user.toDTO());

        // 유튜브 요약 요청 받기

        // 결과 값 리포멧해서 다시 보내기
        @Builder
        record response(
                String id,
                String title,
                String duration,
                String url,
                String description,
                String channel,
                String publishedAt,
                String thumbnail) {
        }

        Map<String, String> data = new HashMap<>();
        return Response.builder()
                .status(HttpStatus.OK)
                .data(data)
                .build();
    }

}
