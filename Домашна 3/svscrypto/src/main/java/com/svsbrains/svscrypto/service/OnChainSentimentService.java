package com.svsbrains.svscrypto.service;

import com.svsbrains.svscrypto.model.dto.OnChainSentimentDto;
import com.svsbrains.svscrypto.model.dto.PredictionResponseDto;

public interface OnChainSentimentService {
    OnChainSentimentDto getAllMetrics(String symbol);
}
