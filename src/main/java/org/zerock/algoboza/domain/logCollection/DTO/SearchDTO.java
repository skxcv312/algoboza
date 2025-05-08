package org.zerock.algoboza.domain.logCollection.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.zerock.algoboza.domain.logCollection.DTO.base.BaseLogDTO;


@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SearchDTO extends BaseLogDTO {
    private String searchText;
}
