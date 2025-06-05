package test.crypto.trade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import test.crypto.trade.entity.PriceSnapshot;
import test.crypto.trade.model.Symbol;

import java.util.List;
import java.util.Optional;

@Repository
public interface PriceSnapshotRepository extends JpaRepository<PriceSnapshot, Long> {


    @Query(nativeQuery = true, value = """
            SELECT ps.*
            FROM price_snapshot ps
            INNER JOIN (
                SELECT symbol, MAX(timestamp) AS max_ts
                FROM price_snapshot
                GROUP BY symbol
            ) latest ON ps.symbol = latest.symbol AND ps.timestamp = latest.max_ts;
            """)
    List<PriceSnapshot> findLatestPriceSnapshotPerSymbol();

    Optional<PriceSnapshot> findTopBySymbolOrderByTimestampDesc(Symbol symbol);
}
