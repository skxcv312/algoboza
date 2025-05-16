package org.zerock.algoboza.entity.redis;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.annotation.Id;
import org.zerock.algoboza.domain.recommend.contents.DTO.KeywordTypeScoreDTO;

@Builder
@RedisHash("KeywordScore")
@Getter
@Setter
public class KeywordScoreRedisEntity {
    @Id
    private Long id;
    private List<KeywordTypeScoreDTO> scoreList;
    private int eventUpdateNum;
}
