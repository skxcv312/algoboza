package org.zerock.algoboza.domain.logCollection.service;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.juli.logging.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.algoboza.domain.logCollection.DTO.base.BaseLogDTO;
import org.zerock.algoboza.domain.logCollection.DTO.base.ClickTrackingDTO;
import org.zerock.algoboza.domain.logCollection.DTO.base.ViewDTO;
import org.zerock.algoboza.entity.UserEntity;
import org.zerock.algoboza.entity.logs.DetailsEntity;
import org.zerock.algoboza.entity.logs.EventEntity;
import org.zerock.algoboza.entity.logs.ClickTrackingEntity;
import org.zerock.algoboza.entity.logs.ViewEntity;
import org.zerock.algoboza.repository.logs.EventRepo;
import org.zerock.algoboza.repository.logs.ClickTrackingRepo;
import org.zerock.algoboza.repository.logs.DetailsRepo;
import org.zerock.algoboza.repository.logs.ViewRepo;

@Log4j2
@Service
@RequiredArgsConstructor
public class BaseLogService {
    private final EventRepo eventRepo;
    private final ClickTrackingRepo clickTrackingRepo;
    private final ViewRepo viewRepo;
    private final DetailsRepo detailsRepo;

    // 클릭 저장
    protected void saveClick(EventEntity eventEntity, ClickTrackingDTO clickTrackingDTO) {
        ClickTrackingEntity clickTrackingEntity = ClickTrackingEntity.builder()
                .action(clickTrackingDTO.getAction())
                .clickTime(clickTrackingDTO.getTimestamp())
                .clickType(clickTrackingDTO.getType())
                .clickUrl(clickTrackingDTO.getUrl())
                .event(eventEntity)
                .build();
        clickTrackingRepo.save(clickTrackingEntity);

    }

    // 디테일 저장
    protected void saveDetails(EventEntity eventEntity, String detail) {
        DetailsEntity detailEntity = DetailsEntity.builder()
                .detail(detail)
                .event(eventEntity)
                .build();
        detailsRepo.save(detailEntity);
    }

    // 뷰 저장
    protected void saveView(EventEntity eventEntity, ViewDTO viewDTO) {
        ViewEntity viewEntity = ViewEntity.builder()
                .event(eventEntity)
                .dwellTime(viewDTO.getDwell_time())
                .startTime(viewDTO.getStart_time())
                .totalScroll(viewDTO.getTotal_scroll())
                .build();
        viewRepo.save(viewEntity);
    }

    // 이벤트 엔티티 저장
    EventEntity saveEvent(UserEntity userEntity, BaseLogDTO baseLogDTO) {
        EventEntity eventEntity = EventEntity.builder()
                .user(userEntity)
                .pageUrl(baseLogDTO.getUrl())
                .timestamp(baseLogDTO.getTimestamp())
                .eventType(baseLogDTO.getType())
                .build();

        return eventRepo.save(eventEntity);
    }

    @Transactional
    public EventEntity saveBaseLog(UserEntity userEntity, BaseLogDTO baseLogDTO) {
        EventEntity eventEntity = saveEvent(userEntity, baseLogDTO);

        // 디테일 로그 저장
        baseLogDTO.getDetails().forEach(detail -> {
            saveDetails(eventEntity, detail);
        });

        // 뷰 로그 저장
        saveView(eventEntity, baseLogDTO.getView());

        // 클릭 트래킹 로그 저장
        baseLogDTO.getClickTracking().forEach(clickTracking -> {
            saveClick(eventEntity, clickTracking);
        });
        return eventEntity;
    }


}
