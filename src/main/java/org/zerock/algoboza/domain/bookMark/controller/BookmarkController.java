package org.zerock.algoboza.domain.bookMark.controller;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zerock.algoboza.domain.auth.service.AuthService;
import org.zerock.algoboza.domain.bookMark.DTO.UserBookMarkDTO;
import org.zerock.algoboza.domain.bookMark.service.BookMarkService;
import org.zerock.algoboza.entity.BookMarkEntity;
import org.zerock.algoboza.entity.UserEntity;
import org.zerock.algoboza.global.JsonUtils;
import org.zerock.algoboza.global.Response;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/save/content")
public class BookmarkController {
    private final JsonUtils jsonUtils;
    private final BookMarkService bookMarkService;
    private final AuthService authService;

    public record postContentRequest(
            List<JsonNode> contents
    ) {
    }

    @PostMapping()
    public Response<?> postContent(@RequestBody postContentRequest Request) {
        UserEntity user = authService.getUserContext();
        for (JsonNode item : Request.contents) {
            bookMarkService.saveContent(user, item);
        }

        return Response.builder()
                .status(HttpStatus.OK)
                .build();
    }

    @GetMapping()
    public Response<?> getContent() {
        UserEntity user = authService.getUserContext();
        UserBookMarkDTO bookMarkList = bookMarkService.lookUpContent(user);

        return Response.builder()
                .status(HttpStatus.OK)
                .data(bookMarkList)
                .build();
    }


    public record DeleteContentRequest(
            List<Integer> contents_id
    ) {
    }

    @DeleteMapping()
    public Response<?> deleteContent(@RequestBody DeleteContentRequest Request) {
        List<Integer> contents_id = Request.contents_id();
        contents_id.forEach(bookMarkService::deleteBookMark);

        return Response.builder()
                .status(HttpStatus.OK)
                .build();
    }
}
