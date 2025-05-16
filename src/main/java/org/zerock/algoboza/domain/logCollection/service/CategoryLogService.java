package org.zerock.algoboza.domain.logCollection.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.zerock.algoboza.domain.logCollection.DTO.CategoryDTO;
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
public class CategoryLogService implements LogProcessor<CategoryDTO> {
    private final BaseLogService baseLogService;
    private final CategoryRepo categoryRepo;

    @Override
    public void saveTypeLog(EmailIntegrationEntity user, CategoryDTO log) {
        EventEntity eventEntity = baseLogService.saveBaseLog(user, log); // 기본 저장

        List<String> categoryList = log.getCategory();
        categoryList.forEach(category -> {
            CategoryEntity categoryEntity = CategoryEntity.builder()
                    .event(eventEntity)
                    .category(category)
                    .build();

            categoryRepo.save(categoryEntity);
        });


    }

    @Override
    public void readLog(UserEntity user, CategoryDTO log) {

    }
}
