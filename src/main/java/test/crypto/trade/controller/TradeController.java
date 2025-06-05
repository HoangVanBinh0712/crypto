package test.crypto.trade.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import test.crypto.trade.entity.TradeTransaction;
import test.crypto.trade.request.TradeRequest;
import test.crypto.trade.service.TradeService;

import java.util.List;

@RestController
@RequestMapping("/api/trade")
public class TradeController {
    @Autowired
    private TradeService tradeService;

    @PostMapping
    public ResponseEntity<?> trade(@Valid @RequestBody TradeRequest request) {
        tradeService.executeTrade(request);
        return ResponseEntity.ok("Trade successful");
    }

    @PostMapping("history/{userId}")
    public ResponseEntity<List<TradeTransaction>> getUserTradingHistory(@PathVariable Long userId) {
        List<TradeTransaction> trades = tradeService.getUserTradeHistory(userId);
        return ResponseEntity.ok(trades);
    }
}