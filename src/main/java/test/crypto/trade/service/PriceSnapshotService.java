package test.crypto.trade.service;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import test.crypto.trade.config.ApiException;
import test.crypto.trade.dto.BinanceTicker;
import test.crypto.trade.dto.HuobiResponse;
import test.crypto.trade.dto.HuobiTicker;
import test.crypto.trade.entity.PriceSnapshot;
import test.crypto.trade.model.Const;
import test.crypto.trade.model.Exchange;
import test.crypto.trade.model.Symbol;
import test.crypto.trade.repository.PriceSnapshotRepository;
import test.crypto.trade.response.LatestBestPriceResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

@Service
@Log4j2
public class PriceSnapshotService {

    @Value("${app.binanceUrl}")
    private String binanceUrl;

    @Value("${app.houbiUrl}")
    private String houbiUrl;

    @Autowired
    private PriceSnapshotRepository priceSnapshotRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CacheService cacheService;

    @Scheduled(fixedRate = 10000)
    @Async
    @Transactional
    public void aggregatePrice() {
        try {

            Map<String, PriceSnapshot> binancePrices = new HashMap<>();
            Map<String, PriceSnapshot> huobiPrices = new HashMap<>();
            fetchAll(binancePrices, huobiPrices);

            for (String symbol : Const.SUPPORTED_SYMBOLS) {
                PriceSnapshot binance = binancePrices.get(symbol);
                PriceSnapshot huobi = huobiPrices.get(symbol);

                if (binance == null && huobi == null) continue;

                // Determine best bid and its source
                PriceSnapshot bestBidSnapshot = Stream.of(binance, huobi)
                        .filter(Objects::nonNull)
                        .max(Comparator.comparing(PriceSnapshot::getBidPrice))
                        .orElse(null);

                // Determine best ask and its source
                PriceSnapshot bestAskSnapshot = Stream.of(binance, huobi)
                        .filter(Objects::nonNull)
                        .min(Comparator.comparing(PriceSnapshot::getAskPrice))
                        .orElse(null);

                PriceSnapshot snapshot = PriceSnapshot.builder()
                        .symbol(Symbol.fromString(symbol))
                        .bidPrice(bestBidSnapshot.getBidPrice())
                        .bidFrom(bestBidSnapshot.getBidFrom())
                        .askPrice(bestAskSnapshot.getAskPrice())
                        .askFrom(bestAskSnapshot.getAskFrom())
                        .timestamp(LocalDateTime.now())
                        .build();

                // clear cache to make api return newest
                cacheService.clear("getLatestBestPrices");
                priceSnapshotRepository.save(snapshot);
                log.info("Saved best price for {}: Bid={}, Ask={}", symbol, bestBidSnapshot.getBidPrice(), bestAskSnapshot.getAskPrice());
            }
        } catch (Exception e) {
            log.error("Error during price aggregation", e);
        }
    }

    public void fetchAll(Map<String, PriceSnapshot> binancePrices, Map<String, PriceSnapshot> huobiPrices) {
        CompletableFuture<Map<String, PriceSnapshot>> binanceFuture = fetchFromBinanceAsync();
        CompletableFuture<Map<String, PriceSnapshot>> huobiFuture = fetchFromHuobiAsync();

        // Wait for all to complete
        CompletableFuture.allOf(binanceFuture, huobiFuture).join();

        try {
            binancePrices.putAll(binanceFuture.get());
            huobiPrices.putAll(huobiFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error combining exchange results", e);
            throw new ApiException(e.getMessage());
        }
    }


    public CompletableFuture<Map<String, PriceSnapshot>> fetchFromBinanceAsync() {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, PriceSnapshot> result = new HashMap<>();
            try {
                BinanceTicker[] response = restTemplate.getForObject(binanceUrl, BinanceTicker[].class);
                if (response != null) {
                    for (BinanceTicker ticker : response) {
                        if (Const.SUPPORTED_SYMBOLS.contains(ticker.getSymbol())) {
                            result.put(ticker.getSymbol(), PriceSnapshot.builder()
                                    .symbol(Symbol.fromString(ticker.getSymbol()))
                                    .bidPrice(new BigDecimal(ticker.getBidPrice()))
                                    .askPrice(new BigDecimal(ticker.getAskPrice()))
                                    .bidFrom(Exchange.BINANCE)
                                    .askFrom(Exchange.BINANCE)
                                    .timestamp(LocalDateTime.now())
                                    .build());
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error fetching from Binance", e);
            }
            return result;
        });
    }

    public CompletableFuture<Map<String, PriceSnapshot>> fetchFromHuobiAsync() {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, PriceSnapshot> result = new HashMap<>();
            try {
                HuobiResponse response = restTemplate.getForObject(houbiUrl, HuobiResponse.class);
                if (response != null && response.getData() != null) {
                    for (HuobiTicker ticker : response.getData()) {
                        String symbol = ticker.getSymbol().toUpperCase();
                        if (Const.BTCUSDT.equals(symbol) || Const.ETHUSDT.equals(symbol)) {
                            result.put(symbol, PriceSnapshot.builder()
                                    .symbol(Symbol.fromString(ticker.getSymbol()))
                                    .bidPrice(ticker.getBid())
                                    .askPrice(ticker.getAsk())
                                    .bidFrom(Exchange.HOUBI)
                                    .askFrom(Exchange.HOUBI)
                                    .timestamp(LocalDateTime.now())
                                    .build());
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error fetching from Huobi", e);
            }
            return result;
        });
    }


    public LatestBestPriceResponse getLatestBestPrices() {
        LatestBestPriceResponse getLatestBestPrices = cacheService.get("getLatestBestPrices", LatestBestPriceResponse.class);
        if (getLatestBestPrices == null) {
            LatestBestPriceResponse response = new LatestBestPriceResponse();
            List<PriceSnapshot> latestPriceSnapshotPerSymbol = priceSnapshotRepository.findLatestPriceSnapshotPerSymbol();
            response.setLatestBestPrices(
                    latestPriceSnapshotPerSymbol.stream().map(
                            x -> modelMapper.map(x, LatestBestPriceResponse.LatestBestPriceDto.class)
                    ).toList());
            cacheService.put("getLatestBestPrices", response);
            return response;
        }
        return getLatestBestPrices;
    }
}
