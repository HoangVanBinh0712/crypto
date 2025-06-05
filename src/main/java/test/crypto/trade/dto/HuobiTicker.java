package test.crypto.trade.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HuobiTicker {
    private String symbol;
    private BigDecimal bid;
    private BigDecimal ask;
}