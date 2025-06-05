package test.crypto.trade.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.crypto.trade.config.ApiException;
import test.crypto.trade.entity.PriceSnapshot;
import test.crypto.trade.entity.TradeTransaction;
import test.crypto.trade.entity.Wallet;
import test.crypto.trade.model.Exchange;
import test.crypto.trade.model.Symbol;
import test.crypto.trade.model.TradeType;
import test.crypto.trade.repository.PriceSnapshotRepository;
import test.crypto.trade.repository.TradeTransactionRepository;
import test.crypto.trade.repository.WalletRepository;
import test.crypto.trade.request.TradeRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class TradeService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TradeTransactionRepository tradeTransactionRepository;

    @Autowired
    private PriceSnapshotRepository priceSnapshotRepository;

    @Transactional
    public void executeTrade(TradeRequest req) {
        PriceSnapshot snapshot = priceSnapshotRepository.findTopBySymbolOrderByTimestampDesc(getCoinSymbol(req.getSymbol()))
                .orElseThrow(() -> new ApiException("Price snapshot not found"));
        Exchange exchange = null;
        BigDecimal price = Objects.equals(TradeType.BUY.getValue(), req.getType()) ? snapshot.getAskPrice() : snapshot.getBidPrice();
        BigDecimal total = price.multiply(req.getQuantity());

        // Wallet check
        Wallet usdtWallet = walletRepository.findByUserIdAndCurrency(req.getUserId(), "USDT")
                .orElseThrow(() -> new ApiException("USDT Wallet not found"));
        // Get the coin wallet. If null create new One
        Wallet coinWallet = getCoinWallet(req);
        if (Objects.equals(TradeType.BUY.getValue(), req.getType())) {
            if (usdtWallet.getBalance().compareTo(total) < 0) throw new ApiException("Not enough USDT");
            usdtWallet.decrease(total);
            coinWallet.increase(req.getQuantity());
            exchange = snapshot.getAskFrom();
        } else {
            if (coinWallet.getBalance().compareTo(req.getQuantity()) < 0) throw new ApiException("Not enough coin");
            coinWallet.decrease(req.getQuantity());
            usdtWallet.increase(total);
            exchange = snapshot.getBidFrom();
        }

        // Save updated wallets
        walletRepository.save(usdtWallet);
        walletRepository.save(coinWallet);

        // Save transaction
        TradeTransaction tx = new TradeTransaction();
        tx.setUserId(req.getUserId());
        tx.setSymbol(getCoinSymbol(req.getSymbol()));
        tx.setType(TradeType.valueOf(req.getType()));
        tx.setExchange(exchange);
        tx.setPrice(price);
        tx.setQuantity(req.getQuantity());
        tx.setTotal(total);
        tx.setTimestamp(LocalDateTime.now());
        tradeTransactionRepository.save(tx);
    }

    private Wallet getCoinWallet(TradeRequest req) {
        Wallet coinWallet = walletRepository.findByUserIdAndCurrency(req.getUserId(), getCoinSymbol(req.getSymbol()).getBaseCurrency())
                .orElse(null);
        if (coinWallet == null) {
            coinWallet = new Wallet();
            coinWallet.setUserId(req.getUserId());
            coinWallet.setCurrency(getCoinSymbol(req.getSymbol()).getBaseCurrency());
        }
        return coinWallet;
    }

    private Symbol getCoinSymbol(String symbol) {
        return Symbol.fromString(symbol);
    }

    public List<TradeTransaction> getUserTradeHistory(Long userId) {
        return tradeTransactionRepository.findByUserId(userId);
    }
}
