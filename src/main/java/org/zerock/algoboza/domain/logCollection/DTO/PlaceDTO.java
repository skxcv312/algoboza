package org.zerock.algoboza.domain.logCollection.DTO;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.zerock.algoboza.domain.logCollection.DTO.base.BaseLogDTO;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PlaceDTO extends BaseLogDTO {
    private String searchText;
    @NotNull
    private PlaceDetailDTO placeDtails;

    @Getter
    @Builder
    public static class PlaceDetailDTO {
        private String address;
        private String name;
        private List<String> category;

    }
}
