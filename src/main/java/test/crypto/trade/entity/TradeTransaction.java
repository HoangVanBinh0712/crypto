package test.crypto.trade.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import test.crypto.trade.model.Exchange;
import test.crypto.trade.model.Symbol;
import test.crypto.trade.model.TradeType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trade_transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "symbol", nullable = false, length = 10)
    private Symbol symbol;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private TradeType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "exchange", nullable = false, length = 10)
    private Exchange exchange;

    @Column(name = "price", nullable = false, precision = 19, scale = 6)
    private BigDecimal price;

    @Column(name = "quantity", nullable = false, precision = 19, scale = 6)
    private BigDecimal quantity;

    @Column(name = "total", nullable = false, precision = 19, scale = 6)
    private BigDecimal total;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @ManyToOne(optional = false, fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "price_snapshot_id", nullable = false)
    private PriceSnapshot priceSnapshot;
}