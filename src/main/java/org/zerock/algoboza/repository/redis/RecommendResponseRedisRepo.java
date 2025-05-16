package org.zerock.algoboza.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.zerock.algoboza.entity.redis.RecommendResponseRedisEntity;

public interface RecommendResponseRedisRepo extends CrudRepository<RecommendResponseRedisEntity, Long> {
}
