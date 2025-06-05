package test.crypto.trade.dto;

import lombok.Data;

@Data
public class BinanceTicker {
    private String symbol;
    private String bidPrice;
    private String askPrice;
}