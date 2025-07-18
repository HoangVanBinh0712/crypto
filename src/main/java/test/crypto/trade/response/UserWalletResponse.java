package test.crypto.trade.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserWalletResponse {

    private Long id;

    private String currency;

    private BigDecimal balance = BigDecimal.ZERO;
}
