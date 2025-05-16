package org.zerock.algoboza.domain.mypage.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.zerock.algoboza.domain.recommend.contents.DTO.KeywordTypeScoreDTO;
import org.zerock.algoboza.domain.recommend.contents.service.RecommendService;
import org.zerock.algoboza.entity.UserEntity;

@Service
@RequiredArgsConstructor
public class ChartService {
    private final RecommendService recommendService;


    // 리스트 조회 메서드
    public List<KeywordTypeScoreDTO> getKeywordScoreList(UserEntity user) {
        return recommendService.getKeywordTypeScore(user);
    }
}
