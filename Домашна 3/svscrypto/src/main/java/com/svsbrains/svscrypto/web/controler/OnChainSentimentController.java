package com.svsbrains.svscrypto.web.controler;

import com.svsbrains.svscrypto.model.dto.OnChainSentimentDto;
import com.svsbrains.svscrypto.service.OnChainSentimentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/on-chain")
public class OnChainSentimentController {
    private final OnChainSentimentService onChainSentimentService;

    public OnChainSentimentController(OnChainSentimentService onChainSentimentService) {
        this.onChainSentimentService = onChainSentimentService;
    }

    @GetMapping
    public ResponseEntity<OnChainSentimentDto> getAllMetricsForSymbol(@RequestParam String symbol) {
        OnChainSentimentDto data =  onChainSentimentService.getAllMetrics(symbol);

        if (data != null) {
            return ResponseEntity.ok(data);
        } else {
            return ResponseEntity.status(503).build();
        }
    }
}
