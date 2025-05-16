package org.zerock.algoboza.domain.logCollection.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.algoboza.domain.logCollection.DTO.base.BaseLogDTO;
import org.zerock.algoboza.domain.logCollection.DTO.base.ClickTrackingDTO;
import org.zerock.algoboza.domain.logCollection.DTO.base.ViewDTO;
import org.zerock.algoboza.entity.EmailIntegrationEntity;
import org.zerock.algoboza.entity.logs.DetailsEntity;
import org.zerock.algoboza.entity.logs.EventEntity;
import org.zerock.algoboza.entity.logs.ClickTrackingEntity;
import org.zerock.algoboza.entity.logs.ViewEntity;
import org.zerock.algoboza.entity.redis.KeywordScoreRedisEntity;
import org.zerock.algoboza.repository.EmailIntegrationRepo;
import org.zerock.algoboza.repository.logs.EventRepo;
import org.zerock.algoboza.repository.logs.ClickTrackingRepo;
import org.zerock.algoboza.repository.logs.DetailsRepo;
import org.zerock.algoboza.repository.logs.ViewRepo;
import org.zerock.algoboza.repository.redis.KeywordScoreRedisRepo;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class BaseLogService {
    private final EventRepo eventRepo;
    private final ClickTrackingRepo clickTrackingRepo;
    private final ViewRepo viewRepo;
    private final DetailsRepo detailsRepo;
    private final EmailIntegrationRepo emailIntegrationRepo;
    private final KeywordScoreRedisRepo keywordScoreRedisRepo;


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
    EventEntity saveEvent(EmailIntegrationEntity emailIntegrationEntity, BaseLogDTO baseLogDTO) {
        log.info("Save event" + baseLogDTO.getType());
        EventEntity eventEntity = EventEntity.builder()
                .emailIntegrationUser(emailIntegrationEntity)
                .pageUrl(baseLogDTO.getUrl())
                .timestamp(baseLogDTO.getTimestamp())
                .eventType(baseLogDTO.getType())
                .build();

        return eventRepo.save(eventEntity);
    }

    public void redisCount(Long id) {
        KeywordScoreRedisEntity entity = keywordScoreRedisRepo.findById(id).orElse(null);
        if (entity == null) {
            return;
        }
        entity.setEventUpdateNum(entity.getEventUpdateNum() + 1);
        keywordScoreRedisRepo.save(entity);
    }

    // 베이스 로그 저장 분기점
    public EventEntity saveBaseLog(EmailIntegrationEntity emailIntegrationEntity, BaseLogDTO baseLogDTO) {
        EventEntity eventEntity = saveEvent(emailIntegrationEntity, baseLogDTO);

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

        // redis event count
        redisCount(emailIntegrationEntity.getUser().getId());
        return eventEntity;
    }

    public EmailIntegrationEntity findUserByLog(BaseLogDTO baseLogDTO) {
        String UserEmail = baseLogDTO.getUserEmail();
        EmailIntegrationEntity a = emailIntegrationRepo.findByEmail(UserEmail);
        return a;
    }


}
