package org.zerock.algoboza.domain.bookMark.DTO;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.zerock.algoboza.entity.BookMarkEntity;

@Builder
public record UserBookMarkDTO(
        List<ObjectNode> contents
) {
}
