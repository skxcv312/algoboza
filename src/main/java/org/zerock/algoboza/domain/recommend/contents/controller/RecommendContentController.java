package org.zerock.algoboza.domain.recommend.contents.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.zerock.algoboza.domain.recommend.contents.DTO.KeywordTypeScoreDTO;
import org.zerock.algoboza.domain.recommend.contents.DTO.TypeKeywordDTO;
import org.zerock.algoboza.domain.recommend.contents.DTO.UserResponse;
import org.zerock.algoboza.domain.recommend.contents.service.RecommendService;
import org.zerock.algoboza.domain.recommend.contents.service.SomeThingElse;
import org.zerock.algoboza.entity.UserEntity;
import org.zerock.algoboza.global.JsonUtils;
import org.zerock.algoboza.global.Response;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommend")
public class RecommendContentController {
    private final RecommendService recommendService;
    private final SomeThingElse someThingElse;
    private final WebClient webClient = WebClient.create();
    private final JsonUtils jsonUtils;

    @GetMapping("")
    public Response<?> getUserRecommendation(
            @AuthenticationPrincipal UserEntity user
    ) {
        // 쇼핑 관심 키워드 추출
        List<KeywordTypeScoreDTO> KeywordTypeScoreDTO = recommendService.getKeywordTypeScore(user);

        // 컨텐츠 조회
        UserResponse response = recommendService.recommendContent(user);
        return Response.builder()
                .status(HttpStatus.OK)
                .data(response)
                .build();

    }

    @GetMapping("/something-else")
    public Response<?> getOtherRecommendation(
            @AuthenticationPrincipal UserEntity user
    ) {
        UserResponse response = someThingElse.getOtherResponse(user);

        return Response.builder()
                .status(HttpStatus.OK)
                .data(someThingElse.adepter(response))
                .build();
    }
}
