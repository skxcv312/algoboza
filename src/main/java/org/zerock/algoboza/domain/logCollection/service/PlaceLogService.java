package org.zerock.algoboza.domain.logCollection.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.algoboza.domain.logCollection.DTO.CartDTO;
import org.zerock.algoboza.domain.logCollection.DTO.PlaceDTO;
import org.zerock.algoboza.domain.logCollection.DTO.PlaceDTO.PlaceDetailDTO;
import org.zerock.algoboza.domain.logCollection.service.impl.LogProcessor;
import org.zerock.algoboza.entity.EmailIntegrationEntity;
import org.zerock.algoboza.entity.UserEntity;
import org.zerock.algoboza.entity.logs.EventEntity;
import org.zerock.algoboza.entity.logs.place.PlaceCategoryEntity;
import org.zerock.algoboza.entity.logs.place.PlaceDetailEntity;
import org.zerock.algoboza.entity.logs.place.PlaceEntity;
import org.zerock.algoboza.repository.logs.PlaceRepo.PlaceCategoryRepo;
import org.zerock.algoboza.repository.logs.PlaceRepo.PlaceDetailRepo;
import org.zerock.algoboza.repository.logs.PlaceRepo.PlaceRepo;

@Service
@RequiredArgsConstructor
@Transactional
public class PlaceLogService implements LogProcessor<PlaceDTO> {
    private final BaseLogService baseLogService;
    private final PlaceCategoryRepo placeCategoryRepo;
    private final PlaceRepo placeRepo;
    private final PlaceDetailRepo placeDetailRepo;

    private void saveCategory(PlaceDetailEntity placeDetailEntity, String category) {
        PlaceCategoryEntity placeCategoryEntity = PlaceCategoryEntity.builder()
                .placeDetail(placeDetailEntity)
                .category(category)
                .build();
        placeCategoryRepo.save(placeCategoryEntity);
    }

    private PlaceDetailEntity saveDetail(PlaceEntity placeEntity, PlaceDetailDTO placeDtails) {
        PlaceDetailEntity placeDetailEntity = PlaceDetailEntity.builder()
                .name(placeDtails.getName())
                .address(placeDtails.getAddress())
                .place(placeEntity)
                .build();

        return placeDetailRepo.save(placeDetailEntity);
    }

    private PlaceEntity savePlace(EventEntity eventEntity, PlaceDTO placeDTO) {
        PlaceEntity placeEntity = PlaceEntity.builder()
                .searchText(placeDTO.getSearchText())
                .event(eventEntity)
                .build();

        return placeRepo.save(placeEntity);
    }

    @Override
    public void saveTypeLog(EmailIntegrationEntity user, PlaceDTO log) {
        EventEntity eventEntity = baseLogService.saveBaseLog(user, log); // 기본 저장

        // 장소 저장
        PlaceEntity placeEntity = savePlace(eventEntity, log);

        // 장소 디테일 저장
        PlaceDetailEntity placeDetailEntity = saveDetail(placeEntity, log.getPlaceDtails());

        // 장소 카테고리 저장
        List<String> categories = log.getPlaceDtails().getCategory();
        categories.forEach(category -> saveCategory(placeDetailEntity, category));


    }

    @Override
    public void readLog(UserEntity user, PlaceDTO log) {

    }
}
