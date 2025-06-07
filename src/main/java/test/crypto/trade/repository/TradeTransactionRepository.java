package test.crypto.trade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import test.crypto.trade.entity.TradeTransaction;

import java.util.List;

@Repository
public interface TradeTransactionRepository extends JpaRepository<TradeTransaction, Long> {

    @Query("SELECT t FROM TradeTransaction t JOIN FETCH t.priceSnapshot WHERE t.user.id = :userId")
    List<TradeTransaction> findByUserId(Long userId);
}
