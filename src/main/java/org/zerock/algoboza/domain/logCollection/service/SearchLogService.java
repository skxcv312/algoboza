package org.zerock.algoboza.domain.logCollection.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.zerock.algoboza.domain.logCollection.DTO.SearchDTO;
import org.zerock.algoboza.domain.logCollection.DTO.base.BaseLogDTO;
import org.zerock.algoboza.domain.logCollection.service.impl.LogProcessor;
import org.zerock.algoboza.entity.EmailIntegrationEntity;
import org.zerock.algoboza.entity.UserEntity;
import org.zerock.algoboza.entity.logs.EventEntity;
import org.zerock.algoboza.entity.logs.search.SearchEntity;
import org.zerock.algoboza.repository.EmailIntegrationRepo;
import org.zerock.algoboza.repository.logs.searchRepo.SearchRepo;

@Service
@RequiredArgsConstructor
public class SearchLogService implements LogProcessor<SearchDTO> {
    private final BaseLogService baseLogService;
    private final SearchRepo searchRepo;


    @Override
    public void saveTypeLog(EmailIntegrationEntity user, SearchDTO log) {
        EventEntity eventEntity = baseLogService.saveBaseLog(user, log); // 기본 저장
        SearchEntity savedSearchEntity = SearchEntity.builder()
                .searchText(log.getSearchText())
                .event(eventEntity)
                .build();

        searchRepo.save(savedSearchEntity);


    }

    @Override
    public void readLog(UserEntity user, SearchDTO log) {

    }
}
