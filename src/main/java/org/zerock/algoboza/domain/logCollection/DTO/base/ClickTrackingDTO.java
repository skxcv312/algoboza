package org.zerock.algoboza.domain.logCollection.DTO.base;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ClickTrackingDTO {
    private String action;
    private LocalDateTime timestamp;
    private String url;
    private String type;
}
