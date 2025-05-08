package org.zerock.algoboza.domain.logCollection.DTO.base;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ViewDTO {
    private int dwell_time;
    private LocalDateTime start_time;
    private double total_scroll;
}
