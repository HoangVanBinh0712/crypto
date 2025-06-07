package test.crypto.trade.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import test.crypto.trade.request.TradeRequest;
import test.crypto.trade.response.UserTradingHistoryResponse;
import test.crypto.trade.service.TradeService;


@RestController
@RequestMapping("/trade")
public class TradeController {
    @Autowired
    private TradeService tradeService;

    @PostMapping
    public ResponseEntity<?> trade(@Valid @RequestBody TradeRequest request) throws JsonProcessingException {

        UserTradingHistoryResponse.UserTradingHistoryDto userTradingHistoryDto = tradeService.executeTrade(request);

        return ResponseEntity.ok(userTradingHistoryDto);
    }

    @PostMapping("history/{userId}")
    public ResponseEntity<UserTradingHistoryResponse> getUserTradingHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(tradeService.getUserTradeHistory(userId));
    }
}