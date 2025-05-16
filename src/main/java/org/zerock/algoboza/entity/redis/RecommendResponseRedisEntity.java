package org.zerock.algoboza.entity.redis;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.zerock.algoboza.domain.recommend.contents.DTO.UserResponse;

@Builder
@RedisHash("recommendResponse")
@Getter
@Setter
public class RecommendResponseRedisEntity {
    @Id
    private Long id;
    private String userResponse;
}
