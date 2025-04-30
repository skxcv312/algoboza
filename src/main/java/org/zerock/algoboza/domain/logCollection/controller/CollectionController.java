package org.zerock.algoboza.domain.logCollection.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.algoboza.global.Response;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/collection/log/")
public class CollectionController {
    @PostMapping("/search")
    public Response<?> getSearchLog(@RequestBody String body) {
        log.info(body);
        return Response.builder()
                .status(HttpStatus.OK)
                .data(body)
                .build();
    }

    @PostMapping("/product")
    public Response<?> getProductLog(@RequestBody String body) {
        log.info(body);
        return Response.builder()
                .status(HttpStatus.OK)
                .data(body)
                .build();
    }

    @PostMapping("/category")
    public Response<?> getCategoryLog(@RequestBody String body) {
        log.info(body);
        return Response.builder()
                .status(HttpStatus.OK)
                .data(body)
                .build();
    }

    @PostMapping("/cart")
    public Response<?> getCartLog(@RequestBody String body) {
        log.info(body);
        return Response.builder()
                .status(HttpStatus.OK)
                .data(body)
                .build();
    }
}
