package org.zerock.algoboza.domain.bookMark.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.algoboza.domain.auth.service.AuthService;
import org.zerock.algoboza.domain.bookMark.DTO.UserBookMarkDTO;
import org.zerock.algoboza.entity.BookMarkEntity;
import org.zerock.algoboza.entity.UserEntity;
import org.zerock.algoboza.global.JsonUtils;
import org.zerock.algoboza.repository.BookMarkRepo;

@Log4j2
@Service
@RequiredArgsConstructor
public class BookMarkService {
    private final JsonUtils jsonUtils;
    private final AuthService authService;
    private final BookMarkRepo bookMarkRepo;

    // 북마크 저장
    public void saveContent(UserEntity user, JsonNode content) {
        String title = content.get("title").asText();
        String link = content.get("link").asText();
        String type = content.get("type").asText();
        String image = content.has("image") ? content.get("image").asText() : null;

        BookMarkEntity bookMarkEntity = BookMarkEntity.builder()
                .user(user)
                .title(title)
                .link(link)
                .type(type)
                .image(image)
                .description(content.toString())
                .build();
        if (isSameContent(bookMarkEntity)) {
            throw new IllegalArgumentException("The same content has already been saved.");
        }
        bookMarkRepo.save(bookMarkEntity);
    }

    // 북마크 삭제
    public void deleteContent() {
        UserEntity user = authService.getUserContext();
        List<BookMarkEntity> bookMarkEntityList = bookMarkRepo.findByUser(user);


    }

    // 북마크 조회
    public UserBookMarkDTO lookUpContent(UserEntity user) {
        List<BookMarkEntity> bookMarkEntityList = bookMarkRepo.findByUser(user);

        List<ObjectNode> contents = bookMarkEntityList.stream()
                .map(bookMark -> {
                    ObjectNode obj = jsonUtils.toObjectNode(bookMark.getDescription());
                    obj.put("id", bookMark.getId());
                    return obj;
                })
                .toList();

        return UserBookMarkDTO.builder()
                .contents(contents)
                .build();
    }

    // 저장시 중복 체크
    public boolean isSameContent(BookMarkEntity bookMarkEntity) {
        return bookMarkRepo.existsByLinkAndUser(bookMarkEntity.getLink(), bookMarkEntity.getUser());
    }


    // 삭제
    public void deleteBookMark(int bookMarkId) {
        bookMarkRepo.deleteById((long) bookMarkId);
    }
}
