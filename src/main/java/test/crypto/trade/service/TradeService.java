package test.crypto.trade.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.crypto.trade.config.ApiException;
import test.crypto.trade.entity.PriceSnapshot;
import test.crypto.trade.entity.TradeTransaction;
import test.crypto.trade.entity.User;
import test.crypto.trade.entity.Wallet;
import test.crypto.trade.model.Exchange;
import test.crypto.trade.model.Symbol;
import test.crypto.trade.model.TradeType;
import test.crypto.trade.repository.PriceSnapshotRepository;
import test.crypto.trade.repository.TradeTransactionRepository;
import test.crypto.trade.repository.UserRepository;
import test.crypto.trade.repository.WalletRepository;
import test.crypto.trade.request.TradeRequest;
import test.crypto.trade.response.UserTradingHistoryResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TradeService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TradeTransactionRepository tradeTransactionRepository;

    @Autowired
    private PriceSnapshotRepository priceSnapshotRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public UserTradingHistoryResponse.UserTradingHistoryDto executeTrade(TradeRequest req) {
        User user = userRepository.getReferenceById(req.getUserId());
        PriceSnapshot snapshot = priceSnapshotRepository.findTopBySymbolOrderByTimestampDesc(getCoinSymbol(req.getSymbol()))
                .orElseThrow(() -> new ApiException("Price snapshot not found"));
        Exchange exchange = null;
        BigDecimal price = TradeType.BUY.equals(TradeType.fromSymbol(req.getType())) ? snapshot.getAskPrice() : snapshot.getBidPrice();
        BigDecimal total = price.multiply(req.getQuantity());

        // Wallet check
        Wallet usdtWallet = walletRepository.findByUserAndCurrency(user, "USDT")
                .orElseThrow(() -> new ApiException("USDT Wallet not found"));
        // Get the coin wallet. If null create new One
        Wallet coinWallet = getCoinWallet(req, user);
        TradeType tradeType = TradeType.fromSymbol(req.getType());
        if (Objects.equals(TradeType.BUY, tradeType)) {
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
        tx.setUser(user);
        tx.setSymbol(getCoinSymbol(req.getSymbol()));
        tx.setType(TradeType.fromSymbol(req.getType()));
        tx.setExchange(exchange);
        tx.setPrice(price);
        tx.setQuantity(req.getQuantity());
        tx.setTotal(total);
        tx.setTimestamp(LocalDateTime.now());
        tx.setPriceSnapshot(snapshot);
        tradeTransactionRepository.save(tx);

        return getUserTradingHistoryDto(tx, snapshot);
    }

    private UserTradingHistoryResponse.UserTradingHistoryDto getUserTradingHistoryDto(TradeTransaction tx, PriceSnapshot snapshot) {
        UserTradingHistoryResponse.UserTradingHistoryDto response = modelMapper.map(tx, UserTradingHistoryResponse.UserTradingHistoryDto.class);
        response.setTradingSnapshot(modelMapper.map(snapshot, UserTradingHistoryResponse.TradingSnapshotDto.class));
        return response;
    }

    private Wallet getCoinWallet(TradeRequest req, User user) {
        Wallet coinWallet = walletRepository.findByUserAndCurrency(user, getCoinSymbol(req.getSymbol()).getBaseCurrency())
                .orElse(null);
        if (coinWallet == null) {
            coinWallet = new Wallet();
            coinWallet.setUser(user);
            coinWallet.setCurrency(getCoinSymbol(req.getSymbol()).getBaseCurrency());
        }
        return coinWallet;
    }

    private Symbol getCoinSymbol(String symbol) {
        return Symbol.fromString(symbol);
    }

    public UserTradingHistoryResponse getUserTradeHistory(Long userId) {

        UserTradingHistoryResponse response = new UserTradingHistoryResponse();
        List<TradeTransaction> tradeHistory = tradeTransactionRepository.findByUserId(userId);
        response.setUserTradingHistory(tradeHistory.stream().map(x -> {
            UserTradingHistoryResponse.UserTradingHistoryDto data = getUserTradingHistoryDto(x, x.getPriceSnapshot());
            return data;
        }).collect(Collectors.toList()));
        return response;
    }
}
