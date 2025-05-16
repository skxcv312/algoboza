package org.zerock.algoboza.domain.logCollection.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class ProductDTO extends BaseLogDTO {
    private String productName;
    private int price;
    private boolean like;
    @NotNull
    @NotEmpty
    private List<String> category;
}
