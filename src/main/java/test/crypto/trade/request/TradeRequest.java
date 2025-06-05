package test.crypto.trade.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import test.crypto.trade.annotations.EnumValue;
import test.crypto.trade.model.Symbol;
import test.crypto.trade.model.TradeType;

import java.math.BigDecimal;

@Data
public class TradeRequest {
    @NotNull(message = "User id must not be null")
    private Long userId;

    @NotBlank(message = "Symbol must not be blank")
    @EnumValue(enumClass = Symbol.class, message = "Symbol must be one of BTCUSDT or ETHUSDT")
    private String symbol;

    @NotBlank(message = "Trade type is required")
    @EnumValue(enumClass = TradeType.class, message = "Trade type must be either BUY or SELL")
    private String type;

    @NotNull(message = "Quantity must not be null")
    @DecimalMin(value = "0.000001", inclusive = false, message = "Quantity must be greater than zero")
    private BigDecimal quantity;
}
