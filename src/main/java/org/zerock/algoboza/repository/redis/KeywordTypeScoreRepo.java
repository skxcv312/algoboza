package org.zerock.algoboza.repository.redis;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.zerock.algoboza.entity.redis.KeywordScoreRedisEntity;
import org.zerock.algoboza.entity.redis.KeywordTypeScoreEntity;


@Repository
public interface KeywordTypeScoreRepo extends JpaRepository<KeywordTypeScoreEntity, Long> {
    List<KeywordTypeScoreEntity> findByKeywordScoreRedisEntity(KeywordScoreRedisEntity keywordScoreRedisEntity);

    void deleteByKeywordScoreRedisEntityId(Long keywordScoreRedisEntity_id);
}
