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
public class CartDTO extends BaseLogDTO {
    private List<CartItemDTO> cart;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CartItemDTO {
        private String brand;
        private String id;
        private String name;
        private int price;
        private int quantity;
        private int discountPrice;
        @NotNull
        @NotEmpty
        private List<String> category;
        private List<String> season;
    }
}
