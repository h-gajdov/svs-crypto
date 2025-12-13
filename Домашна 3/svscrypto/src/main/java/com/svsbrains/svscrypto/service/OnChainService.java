package com.svsbrains.svscrypto.service;

import com.svsbrains.svscrypto.model.dto.OnChainMetricsDto;
import com.svsbrains.svscrypto.model.dto.OnChainSentimentDto;

public interface OnChainService {
    OnChainMetricsDto getAllMetrics(String symbol);

    OnChainSentimentDto getSentimentFromMetrics(String symbol);
}
