package org.zerock.algoboza.domain.youtube.controller;


import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.zerock.algoboza.domain.auth.DTO.UserDTO;
import org.zerock.algoboza.domain.auth.service.AuthService;
import org.zerock.algoboza.domain.youtube.DTO.InterestScoresDTO;
import org.zerock.algoboza.domain.youtube.DTO.VideoInfoDTO;
import org.zerock.algoboza.domain.youtube.DTO.VideoSummaryDTO;
import org.zerock.algoboza.domain.youtube.DTO.VideoSummaryDTO.Meta;
import org.zerock.algoboza.domain.youtube.service.RecommendYoutubeService;
import org.zerock.algoboza.global.JsonUtils;
import org.zerock.algoboza.global.Response;

import reactor.core.publisher.Mono;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/recommend/youtube")
public class RecommendYoutube {
    private final RecommendYoutubeService recommendYoutubeService;
    private final AuthService authService;
    private final JsonUtils jsonUtils;

    @GetMapping()
    public Response<?> recommendYoutube() {
        UserDTO user = authService.getUserContext().toDTO();

        // 유저 관심도 얻기
        InterestScoresDTO interestScores = recommendYoutubeService.getInterestScores(user);

        // 유튜브 요약 요청 받기
        Mono<VideoInfoDTO> response = recommendYoutubeService.getYoutubeVideos(interestScores);

        // 결과 값 리포맷해서 다시 보내기
        return Response.builder()
                .status(HttpStatus.OK)
                .data(Objects.requireNonNull(response.block()).data())
                .message("유튜브 추천")
                .build();
    }

    @GetMapping("/summary")
    public Response<?> recommendYoutubeSummary(String video_id) {
        // 요약하기
        Mono<VideoSummaryDTO> response = recommendYoutubeService.getVideoSummary(video_id);

        return Response.builder()
                .status(HttpStatus.OK)
                .message("YouTube Summary")
                .data(Objects.requireNonNull(response.block()).data())
                .build();
    }

}

//        return response.flatMap(videoInfoDTO -> {
//            log.info("Recommend Youtube video response: {}", videoInfoDTO);
//            ResponseEntity<?> res = ResponseEntity.ok().body(videoInfoDTO);
//            return Mono.justOrEmpty(res);
//        });

//        return response
//                .map(videoInfoDTO -> Response.builder()
//                        .status(HttpStatus.OK)
//                        .message("유튜브 추천")
//                        .data(videoInfoDTO)
//                        .build())
//                .onErrorResume(error -> {
//                    String errorMessage = (error != null && error.getMessage() != null)
//                            ? error.getMessage()
//                            : "Unknown Error";
//                    log.error("유튜브 API 요청 중 오류 발생: {}", errorMessage);
//                    return Mono.just(Response.builder()
//                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                            .message(errorMessage)
//                            .build());
//                });
//        @Builder
//        record response(
//                String id,
//                String title,
//                String duration,
//                String url,
//                String description,
//                String channel,
//                String published_at,
//                String thumbnail
//        ) {
//        }
