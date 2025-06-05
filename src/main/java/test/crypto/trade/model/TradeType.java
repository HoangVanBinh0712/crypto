package test.crypto.trade.model;

import lombok.Getter;
import test.crypto.trade.config.ApiException;

@Getter
public enum TradeType {
    BUY("BUY"),
    SELL("SELL");

    private final String value;

    TradeType(String value) {
        this.value = value;
    }

    public static TradeType fromString(String value) {
        for (TradeType type : TradeType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new ApiException("Unknown trade type: " + value);
    }
}