package com.svsbrains.svscrypto.web.controller;

import com.svsbrains.svscrypto.model.dto.*;
import com.svsbrains.svscrypto.service.OnChainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.svsbrains.svscrypto.util.ResponseUtils.respondOrServiceUnavailable;

/**
 * REST controller that exposes endpoints for retrieving on-chain cryptocurrency data.
 * <p>
 * This controller provides endpoints for metrics, whale movements, exchange flows,
 * news sentiment estimates, and aggregated sentiment indicators for a given cryptocurrency symbol.
 * Responses are automatically wrapped using {@link com.svsbrains.svscrypto.util.ResponseUtils#respondOrServiceUnavailable(Object)},
 * returning HTTP 200 OK if data is present or HTTP 503 Service Unavailable if the data is missing.
 * </p>
 * <p>
 * All endpoints are prefixed with <code>/api/on-chain</code>.
 * </p>
 */
@RestController
@RequestMapping("/api/on-chain")
public class OnChainDataController {
    private final OnChainService onChainService;

    public OnChainDataController(OnChainService onChainService) {
        this.onChainService = onChainService;
    }

    @GetMapping("/metrics")
    public ResponseEntity<OnChainMetricsDto> getAllMetricsFromSymbol(@RequestParam String symbol) {
        return respondOrServiceUnavailable(onChainService.getAllMetrics(symbol));
    }

    @GetMapping("/whale-movements")
    public ResponseEntity<List<WhaleMovementDto>> getWhaleMovements() {
        return respondOrServiceUnavailable(onChainService.getWhaleMovements());
    }

    @GetMapping("/exchange-flows")
    public ResponseEntity<ExchangeFlowDto> getExchangeFlows(@RequestParam String symbol) {
        return respondOrServiceUnavailable(onChainService.getExchangeFlows(symbol));
    }

    @GetMapping("/news")
    public ResponseEntity<EstimateNewsDto> getNewsEstimate(@RequestParam String symbol) {
        return respondOrServiceUnavailable(onChainService.estimateNews(symbol));
    }

    @GetMapping("/sentiment")
    public ResponseEntity<OnChainSentimentDto> getSentimentFromMetrics(@RequestParam String symbol) {
        return respondOrServiceUnavailable(onChainService.getSentimentFromMetrics(symbol));
    }
}
