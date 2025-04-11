package org.zerock.algoboza.domain.youtube.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.zerock.algoboza.domain.auth.DTO.UserDTO;
import org.zerock.algoboza.domain.youtube.DTO.InterestScoresDTO;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendYoutubeService {
    // 유저 정보를 토대로 점수 주기
    public InterestScoresDTO getInterestScores(UserDTO user) {

        List<InterestScoresDTO.keyword> keywords = new ArrayList<>();

        keywords.add(new InterestScoresDTO.keyword("봄", 40));
        keywords.add(new InterestScoresDTO.keyword("옷", 60));
        keywords.add(new InterestScoresDTO.keyword("청바지", 80));


        return InterestScoresDTO.builder()
                .interest_scores(keywords)
                .build();
    }

    // 점수 매기기

    //
}
