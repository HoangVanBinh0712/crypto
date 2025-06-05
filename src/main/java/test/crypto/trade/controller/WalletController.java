package test.crypto.trade.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import test.crypto.trade.service.WalletService;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserWalletBalances(@PathVariable Long userId) {
        return ResponseEntity.ok(walletService.getUserWallets(userId));
    }
}