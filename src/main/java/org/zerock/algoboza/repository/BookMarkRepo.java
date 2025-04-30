package org.zerock.algoboza.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.algoboza.entity.BookMarkEntity;
import org.zerock.algoboza.entity.UserEntity;

@Repository
public interface BookMarkRepo extends JpaRepository<BookMarkEntity, Long> {
    List<BookMarkEntity> findByUser(UserEntity user);

    boolean existsByLinkAndUser(String link, UserEntity user);

    void deleteById(Long id);
}
