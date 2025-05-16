package org.zerock.algoboza.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.zerock.algoboza.entity.redis.KeywordScoreRedisEntity;

public interface KeywordScoreRedisRepo extends CrudRepository<KeywordScoreRedisEntity, Long> {
}
