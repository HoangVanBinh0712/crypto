package test.crypto.trade.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import test.crypto.trade.response.LatestBestPriceResponse;
import test.crypto.trade.service.PriceSnapshotService;


@RestController
@RequestMapping("/prices")
public class PriceController {

    private final PriceSnapshotService priceSnapshotService;

    public PriceController(PriceSnapshotService priceSnapshotService) {
        this.priceSnapshotService = priceSnapshotService;
    }

    @GetMapping("/best")
    public ResponseEntity<LatestBestPriceResponse> getLatestBestPrices() {
        return ResponseEntity.ok(priceSnapshotService.getLatestBestPrices());
    }
}
