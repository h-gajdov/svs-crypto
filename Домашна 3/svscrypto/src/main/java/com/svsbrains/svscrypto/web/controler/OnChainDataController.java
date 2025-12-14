package com.svsbrains.svscrypto.web.controler;

import com.svsbrains.svscrypto.model.dto.*;
import com.svsbrains.svscrypto.service.OnChainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/on-chain")
public class OnChainDataController {
    private final OnChainService onChainService;

    public OnChainDataController(OnChainService onChainService) {
        this.onChainService = onChainService;
    }

    @GetMapping("/metrics")
    public ResponseEntity<OnChainMetricsDto> getAllMetricsFromSymbol(@RequestParam String symbol) {
        OnChainMetricsDto data = onChainService.getAllMetrics(symbol);

        if (data != null) {
            return ResponseEntity.ok(data);
        } else {
            return ResponseEntity.status(503).build();
        }
    }

    @GetMapping("/whale-movements")
    public ResponseEntity<List<WhaleMovementDto>> getWhaleMovements() {
        List<WhaleMovementDto> data = onChainService.getWhaleMovements();

        if (data != null) {
            return ResponseEntity.ok(data);
        } else {
            return ResponseEntity.status(503).build();
        }
    }

    @GetMapping("/exchange-flows")
    public ResponseEntity<ExchangeFlowDto> getExchangeFlows(@RequestParam String symbol) {
        ExchangeFlowDto data = onChainService.getExchangeFlows(symbol);

        if (data != null) {
            return ResponseEntity.ok(data);
        } else {
            return ResponseEntity.status(503).build();
        }
    }

    @GetMapping("/news")
    public ResponseEntity<EstimateNewsDto> getNewsEstimate(@RequestParam String symbol) {
        EstimateNewsDto data = onChainService.estimateNews(symbol);

        if (data != null) {
            return ResponseEntity.ok(data);
        } else {
            return ResponseEntity.status(503).build();
        }
    }

    @GetMapping("/sentiment")
    public ResponseEntity<OnChainSentimentDto> getSentimentFromSymbol(@RequestParam String symbol) {
        OnChainSentimentDto data = onChainService.getSentimentFromMetrics(symbol);

        if (data != null) {
            return ResponseEntity.ok(data);
        } else {
            return ResponseEntity.status(503).build();
        }
    }
}
