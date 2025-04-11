package org.zerock.algoboza.domain.mypage.DTO;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record GetMyInfoResponse(
        String name,
        String email,
        String birth_date,
        LocalDateTime creat_at,
        List<ConnectionEmail> connection_email
) {
    @Builder
    public record ConnectionEmail(
            String email,
            String platform,
            LocalDateTime created_at
    ) {
    }
}
