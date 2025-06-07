package test.crypto.trade.response;

import lombok.Data;
import test.crypto.trade.model.Exchange;
import test.crypto.trade.model.Symbol;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class LatestBestPriceResponse {

    @Data
    public static class LatestBestPriceDto {
        private Long id;

        private Symbol symbol;

        private Exchange bidFrom;

        private BigDecimal bidPrice;

        private Exchange askFrom;

        private BigDecimal askPrice;

        private LocalDateTime timestamp;
    }

    private List<LatestBestPriceDto> latestBestPrices;
}
