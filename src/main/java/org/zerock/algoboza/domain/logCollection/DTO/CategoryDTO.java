package org.zerock.algoboza.domain.logCollection.DTO;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.zerock.algoboza.domain.logCollection.DTO.base.BaseLogDTO;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO extends BaseLogDTO {
    private List<String> category;
}
