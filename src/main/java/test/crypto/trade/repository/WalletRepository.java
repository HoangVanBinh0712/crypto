package test.crypto.trade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import test.crypto.trade.entity.User;
import test.crypto.trade.entity.Wallet;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByUserAndCurrency(User user, String currency);

    List<Wallet> findByUser(User user);
}
