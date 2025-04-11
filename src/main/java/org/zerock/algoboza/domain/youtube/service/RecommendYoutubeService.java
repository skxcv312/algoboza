package org.zerock.algoboza.domain.youtube.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.zerock.algoboza.domain.auth.DTO.UserDTO;
import org.zerock.algoboza.domain.youtube.DTO.InterestScoresDTO;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecommendYoutubeService {
    // 유저 정보를 토대로 점수 주기
    public InterestScoresDTO getInterestScores(UserDTO user) {

        // 일단 예시로
        Map<String, Integer> interestScores = new HashMap<>();
        interestScores.put("ai", 87);
        interestScores.put("mcp", 70);
        interestScores.put("창업", 56);
        interestScores.put("프로그래머", 78);

        return new InterestScoresDTO(interestScores);
    }

    // 유튜브 요약


}
