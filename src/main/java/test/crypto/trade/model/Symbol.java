package test.crypto.trade.model;

import lombok.Getter;
import test.crypto.trade.config.ApiException;

@Getter
public enum Symbol {
    BTCUSDT("BTC", "USDT"),
    ETHUSDT("ETH", "USDT");

    private final String baseCurrency;
    private final String quoteCurrency;

    Symbol(String baseCurrency, String quoteCurrency) {
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
    }

    public static Symbol fromString(String symbolStr) {
        for (Symbol s : values()) {
            if (s.name().equalsIgnoreCase(symbolStr)) {
                return s;
            }
        }
        throw new ApiException("Unsupported symbol: " + symbolStr);
    }
}