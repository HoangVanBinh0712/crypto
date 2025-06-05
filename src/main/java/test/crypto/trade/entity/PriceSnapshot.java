package test.crypto.trade.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import test.crypto.trade.model.Exchange;
import test.crypto.trade.model.Symbol;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_snapshot")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "symbol", nullable = false, length = 10)
    private Symbol symbol;

    @Enumerated(EnumType.STRING)
    @Column(name = "bid_from", nullable = false, length = 10)
    private Exchange bidFrom;

    @Column(name = "bid_price", nullable = false, precision = 19, scale = 6)
    private BigDecimal bidPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "ask_from", nullable = false, length = 10)
    private Exchange askFrom;

    @Column(name = "ask_price", nullable = false, precision = 19, scale = 6)
    private BigDecimal askPrice;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

}