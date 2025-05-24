package org.zerock.algoboza.repository.redis;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.zerock.algoboza.entity.redis.KeywordScoreRedisEntity;

@Repository
public interface KeywordScoreRedisRepo extends JpaRepository<KeywordScoreRedisEntity, Long> {
}
