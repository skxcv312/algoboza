package org.zerock.algoboza.domain.recommend.interestTracking.Service.core;

import lombok.Getter;

@Getter
public enum LogActionWeight {
    SEARCH("search", 5),
    PRODUCT("product", 3),
    CATEGORY("category", 2),
    CART("cart", 7),
    PLACE("naver_place", 4),


    DWELL_TIME("dwell_time", 0.1),
    TOTAL_SCROLL("total_scroll", 0.01),
    LIKE("like", 4),
    QUANTITY("quantity", 5),
    PLACE_ADDRESS("address", 6),
    PLACE_CATEGORY("category", 5),
    CLICK_MORE_SEE("상품정보더보기", 4),
    CLICK_CART("장바구니", 5),
    ;


    private final String action;
    private final double weights;

    LogActionWeight(String action, double weights) {
        this.action = action;
        this.weights = weights;
    }

    public static LogActionWeight getWeightByType(String eventType) {
        for (LogActionWeight LogAction : values()) {
            if (eventType.equals(LogAction.getAction())) {
                return LogAction;
            }
        }
        throw new IllegalArgumentException("Invalid log action type: " + eventType);
    }

}

