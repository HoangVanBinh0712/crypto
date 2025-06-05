package test.crypto.trade.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import test.crypto.trade.entity.PriceSnapshot;
import test.crypto.trade.service.PriceSnapshotService;

import java.util.List;

@RestController
@RequestMapping("/prices")
public class PriceController {

    private final PriceSnapshotService priceSnapshotService;

    public PriceController(PriceSnapshotService priceSnapshotService) {
        this.priceSnapshotService = priceSnapshotService;
    }

    @GetMapping("/best")
    public ResponseEntity<List<PriceSnapshot>> getLatestBestPrices() {
        List<PriceSnapshot> prices = priceSnapshotService.getLatestBestPrices();
        return ResponseEntity.ok(prices);
    }
}
