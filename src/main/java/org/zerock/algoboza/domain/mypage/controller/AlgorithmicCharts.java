package org.zerock.algoboza.domain.mypage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.algoboza.domain.mypage.service.ChartService;
import org.zerock.algoboza.entity.UserEntity;
import org.zerock.algoboza.global.Response;

@RestController
@RequiredArgsConstructor
@RequestMapping("/my-page/chart")
public class AlgorithmicCharts {
    private final ChartService chartService;

    @GetMapping
    public Response<?> getCharts(
            @AuthenticationPrincipal UserEntity user
    ) {

        return Response.builder()
                .status(HttpStatus.OK)
                .data(chartService.getKeywordScoreList(user))
                .build();
    }
}
