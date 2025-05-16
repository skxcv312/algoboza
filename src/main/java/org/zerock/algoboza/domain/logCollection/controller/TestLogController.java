package org.zerock.algoboza.domain.logCollection.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.algoboza.domain.auth.service.AuthService;
import org.zerock.algoboza.domain.logCollection.DTO.base.BaseLogDTO;
import org.zerock.algoboza.domain.logCollection.service.BaseLogService;
import org.zerock.algoboza.entity.UserEntity;
import org.zerock.algoboza.global.Response;
import org.zerock.algoboza.repository.UserRepo;
import org.zerock.algoboza.repository.logs.ClickTrackingRepo;
import org.zerock.algoboza.repository.logs.DetailsRepo;
import org.zerock.algoboza.repository.logs.EventRepo;
import org.zerock.algoboza.repository.logs.ViewRepo;
import org.zerock.algoboza.repository.logs.cartRepo.CartItemCategoryRepo;
import org.zerock.algoboza.repository.logs.cartRepo.CartItemRepo;
import org.zerock.algoboza.repository.logs.cartRepo.CartItemSeasonRepo;
import org.zerock.algoboza.repository.logs.categoryRepo.CategoryRepo;
import org.zerock.algoboza.repository.logs.productRepo.ProductRepo;
import org.zerock.algoboza.repository.logs.searchRepo.SearchRepo;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/log/collection/")
public class TestLogController {
    private final BaseLogService baseLogService;
    private final CartItemSeasonRepo cartItemSeasonRepo;
    private final CartItemRepo cartItemRepo;
    private final CartItemCategoryRepo cartItemCategoryRepo;
    private final CategoryRepo categoryRepo;
    private final ClickTrackingRepo clickTrackingRepo;
    private final DetailsRepo detailsRepo;
    private final EventRepo eventRepo;
    private final ProductRepo productRepo;
    private final SearchRepo searchRepo;
    private final ViewRepo viewRepo;


    @DeleteMapping("/test/{email}")
    @Transactional
    public Response<?> clearLog(@PathVariable String email) {
        BaseLogDTO baseLogDTO = BaseLogDTO.builder().userEmail(email).build();
        Long UserId = baseLogService.findUserByLog(baseLogDTO).getId();

        eventRepo.deleteByEmailIntegrationUserId(UserId);

        return Response.builder()
                .status(HttpStatus.OK)
                .data("clear")
                .build();
    }

}
