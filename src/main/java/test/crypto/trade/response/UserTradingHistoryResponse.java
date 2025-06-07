package test.crypto.trade.response;

import lombok.Data;
import test.crypto.trade.model.Exchange;
import test.crypto.trade.model.Symbol;
import test.crypto.trade.model.TradeType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserTradingHistoryResponse {

    @Data
    public static class UserTradingHistoryDto {
        private Long id;

        private Symbol symbol;

        private TradeType type;

        private Exchange exchange;

        private BigDecimal price;

        private BigDecimal quantity;

        private BigDecimal total;

        private LocalDateTime timestamp;

        private TradingSnapshotDto tradingSnapshot;
    }

    @Data
    public static class TradingSnapshotDto {
        private Long id;

        private Symbol symbol;

        private Exchange bidFrom;

        private BigDecimal bidPrice;

        private Exchange askFrom;

        private BigDecimal askPrice;

        private LocalDateTime timestamp;
    }

    private List<UserTradingHistoryDto> userTradingHistory;
}
