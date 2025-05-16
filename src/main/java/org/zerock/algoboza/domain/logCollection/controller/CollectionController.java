package org.zerock.algoboza.domain.logCollection.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.algoboza.domain.auth.service.AuthService;
import org.zerock.algoboza.domain.logCollection.DTO.CartDTO;
import org.zerock.algoboza.domain.logCollection.DTO.CategoryDTO;
import org.zerock.algoboza.domain.logCollection.DTO.PlaceDTO;
import org.zerock.algoboza.domain.logCollection.DTO.ProductDTO;
import org.zerock.algoboza.domain.logCollection.DTO.SearchDTO;
import org.zerock.algoboza.domain.logCollection.service.BaseLogService;
import org.zerock.algoboza.domain.logCollection.service.CartLogService;
import org.zerock.algoboza.domain.logCollection.service.CategoryLogService;
import org.zerock.algoboza.domain.logCollection.service.PlaceLogService;
import org.zerock.algoboza.domain.logCollection.service.ProductLogService;
import org.zerock.algoboza.domain.logCollection.service.SearchLogService;
import org.zerock.algoboza.domain.logCollection.service.impl.LogProcessor;
import org.zerock.algoboza.entity.EmailIntegrationEntity;
import org.zerock.algoboza.entity.UserEntity;
import org.zerock.algoboza.global.Response;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/log/collection/")
public class CollectionController {
    private final BaseLogService baseLogService;
    private final ProductLogService productLogService;
    private final SearchLogService searchLogService;
    private final CartLogService cartLogService;
    private final CategoryLogService categoryLogService;
    private final PlaceLogService placeLogService;

    @PostMapping("/search")
    public Response<?> searchLog(@RequestBody SearchDTO searchLogDTO) {
        EmailIntegrationEntity user = baseLogService.findUserByLog(searchLogDTO);

        // 로그 저장
        searchLogService.saveTypeLog(user, searchLogDTO);
        return Response.builder()
                .status(HttpStatus.OK)
                .data(searchLogDTO)
                .build();
    }

    @PostMapping("/product")
    public Response<?> productLog(@RequestBody ProductDTO productLogDTO) {
        EmailIntegrationEntity user = baseLogService.findUserByLog(productLogDTO);

        productLogService.saveTypeLog(user, productLogDTO);

        return Response.builder()
                .status(HttpStatus.OK)
                .data(productLogDTO)
                .build();
    }

    @PostMapping("/category")
    public Response<?> categoryLog(@RequestBody CategoryDTO categoryLogDTO) {
        EmailIntegrationEntity user = baseLogService.findUserByLog(categoryLogDTO);

        categoryLogService.saveTypeLog(user, categoryLogDTO);

        return Response.builder()
                .status(HttpStatus.OK)
                .data(categoryLogDTO)
                .build();
    }

    @PostMapping("/cart")
    public Response<?> cartLog(@RequestBody CartDTO cartLogDTO) {
        EmailIntegrationEntity user = baseLogService.findUserByLog(cartLogDTO);
        cartLogService.saveTypeLog(user, cartLogDTO);
        return Response.builder()
                .status(HttpStatus.OK)
                .data(cartLogDTO)
                .build();
    }

    @PostMapping("/naver_place")
    public Response<?> cartLog(@RequestBody PlaceDTO placeDTO) {
//        EmailIntegrationEntity user = baseLogService.findUserByLog(placeDTO);
//        placeLogService.saveTypeLog(user, placeDTO);
        return Response.builder()
                .status(HttpStatus.OK)
                .data(placeDTO)
                .build();
    }

}
