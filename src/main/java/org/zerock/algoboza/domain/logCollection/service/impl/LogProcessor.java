package org.zerock.algoboza.domain.logCollection.service.impl;

import lombok.Builder;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.algoboza.domain.logCollection.DTO.base.BaseLogDTO;
import org.zerock.algoboza.domain.logCollection.service.BaseLogService;
import org.zerock.algoboza.entity.EmailIntegrationEntity;
import org.zerock.algoboza.entity.UserEntity;
import org.zerock.algoboza.entity.logs.EventEntity;

public interface LogProcessor<T extends BaseLogDTO> {

    @Transactional
    void saveTypeLog(EmailIntegrationEntity user, T log);

    void readLog(UserEntity user, T log);
}
