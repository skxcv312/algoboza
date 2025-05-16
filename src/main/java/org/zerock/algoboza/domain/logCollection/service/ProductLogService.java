package org.zerock.algoboza.domain.logCollection.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.zerock.algoboza.domain.logCollection.DTO.ProductDTO;
import org.zerock.algoboza.domain.logCollection.DTO.base.BaseLogDTO;
import org.zerock.algoboza.domain.logCollection.service.impl.LogProcessor;
import org.zerock.algoboza.entity.EmailIntegrationEntity;
import org.zerock.algoboza.entity.UserEntity;
import org.zerock.algoboza.entity.logs.EventEntity;
import org.zerock.algoboza.entity.logs.category.CategoryEntity;
import org.zerock.algoboza.entity.logs.product.ProductEntity;
import org.zerock.algoboza.repository.logs.categoryRepo.CategoryRepo;
import org.zerock.algoboza.repository.logs.productRepo.ProductRepo;

@Service
@RequiredArgsConstructor
public class ProductLogService implements LogProcessor<ProductDTO> {
    private final BaseLogService baseLogService;
    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;

    @Override
    public void saveTypeLog(EmailIntegrationEntity user, ProductDTO log) {
        EventEntity eventEntity = baseLogService.saveBaseLog(user, log); // 기본 저장

        ProductEntity productEntity = ProductEntity.builder()
                .productUrl(log.getUrl())
                .price(log.getPrice())
                .name(log.getProductName())
                .like(log.isLike())
                .event(eventEntity)
                .build();
        productRepo.save(productEntity);

        // 카테고리 저장
        log.getCategory().forEach((category) -> {
            CategoryEntity categoryEntity = CategoryEntity.builder()
                    .event(eventEntity)
                    .category(category)
                    .build();
            categoryRepo.save(categoryEntity);
        });

    }

    @Override
    public void readLog(UserEntity user, ProductDTO log) {

    }

//    // search 로그 저장
//    public void saveSearchLog(SearchDTO searchLogDTO) {
//
//    }
//
//    // category 로그 저장
//    public void saveCategoryLog(CategoryDTO categoryLogDTO) {
//    }
//
//    // cart 로그 저장
//    public void saveCartLog(CartDTO cartLogDTO) {
//
//    }
    // 로그 조회

}
