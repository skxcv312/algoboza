package org.zerock.algoboza.domain.logCollection.DTO.base;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class BaseLogDTO {
    private String userEmail;
    private LocalDateTime timestamp;
    private String url;
    private String type;
    private List<String> details;
    private ViewDTO view;
    private List<ClickTrackingDTO> clickTracking;
}
