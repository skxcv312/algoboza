package org.zerock.algoboza.domain.recommend.youtube.service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.zerock.algoboza.domain.recommend.contents.DTO.KeywordTypeScoreDTO;
import org.zerock.algoboza.domain.recommend.interestTracking.DTO.KeywordScoreDTO;
import org.zerock.algoboza.domain.recommend.interestTracking.InterestKeywordService;
import org.zerock.algoboza.domain.recommend.youtube.DTO.InterestScoresDTO;

import java.util.HashMap;
import java.util.Map;
import org.zerock.algoboza.domain.recommend.youtube.DTO.VideoInfoDTO;
import org.zerock.algoboza.domain.recommend.youtube.DTO.VideoSummaryDTO;
import org.zerock.algoboza.entity.UserEntity;
import org.zerock.algoboza.global.JsonUtils;
import org.zerock.algoboza.global.config.WebClientConfig;
import reactor.core.publisher.Mono;

@Log4j2
@Service
@RequiredArgsConstructor
public class RecommendYoutubeService {
    private final WebClientConfig webClientConfig;
    private final InterestKeywordService interestKeywordService;
    private final JsonUtils jsonUtils;

    private final int MAX_SEARCH_KEYWORD = 3;
    private final int MAX_RESULTS = 15;


    // 유저 정보를 토대로 점수 주기
    public InterestScoresDTO getInterestScores(UserEntity userEntity) {
        List<KeywordTypeScoreDTO> keywordScoreDTOList = interestKeywordService.getInterestScore(userEntity);

        Map<String, Integer> interestScores = new HashMap<>();
        keywordScoreDTOList.forEach(keywordScoreDTO -> {
            interestScores.put(keywordScoreDTO.keyword(), (int) keywordScoreDTO.score());
        });

        return new InterestScoresDTO(interestScores);
    }

    // 유튜브 요약
    public Mono<VideoSummaryDTO> getVideoSummary(String videoId) {
        WebClient webClient = webClientConfig.YoutubeBuilder();
        String path = "/api/recommend/youtube/summary";

        return webClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path(path)
                                .queryParam("video_id", videoId)
                                .build()
                ).retrieve()
                .bodyToMono(VideoSummaryDTO.class);

    }


    // 영상 추천 uri 연결
    public Mono<VideoInfoDTO> getYoutubeVideos(InterestScoresDTO interestScoresDTO) {
        WebClient webClient = webClientConfig.YoutubeBuilder();
        String path = "/api/recommend/youtube";
        int max_search_keyword = MAX_SEARCH_KEYWORD;
        int max_results = MAX_RESULTS;

        // 정상 응답 처리
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path(path)
                        .queryParam("max_search_keyword", max_search_keyword)
                        .queryParam("max_results", max_results)
                        .build()
                )
                .bodyValue(interestScoresDTO)
                .retrieve()
                .bodyToMono(VideoInfoDTO.class); // 자동으로 에러 처리됨


    }


}
