package com.svsbrains.svscrypto.service;

import com.svsbrains.svscrypto.model.dto.*;

import java.util.List;

public interface OnChainService {
    OnChainMetricsDto getAllMetrics(String symbol);

    OnChainSentimentDto getSentimentFromMetrics(String symbol);

    EstimateNewsDto estimateNews(String symbol);
    
    ExchangeFlowDto getExchangeFlows(String symbol);
    
    List<WhaleMovementDto> getWhaleMovements();
}
