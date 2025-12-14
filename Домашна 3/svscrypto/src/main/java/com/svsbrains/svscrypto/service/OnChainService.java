package com.svsbrains.svscrypto.service;

import com.svsbrains.svscrypto.model.dto.EstimateNewsDto;
import com.svsbrains.svscrypto.model.dto.OnChainMetricsDto;
import com.svsbrains.svscrypto.model.dto.OnChainSentimentDto;
import com.svsbrains.svscrypto.model.dto.WhaleMovementDto;

import java.util.List;

public interface OnChainService {
    OnChainMetricsDto getAllMetrics(String symbol);

    OnChainSentimentDto getSentimentFromMetrics(String symbol);

    EstimateNewsDto estimateNews(String symbol);

    List<WhaleMovementDto> getWhaleMovements();
}
