package com.svsbrains.svscrypto.service.impl;

import com.svsbrains.svscrypto.client.AnalysisEngineClient;
import com.svsbrains.svscrypto.client.AnalysisEngineEndpoints;
import com.svsbrains.svscrypto.model.dto.*;
import com.svsbrains.svscrypto.service.OnChainService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OnChainMetricsServiceImpl implements OnChainService {
    private final AnalysisEngineClient client;
    private final AnalysisEngineEndpoints endpoints;

    @Override
    public OnChainMetricsDto getAllMetrics(String symbol) {
        return client.get(
                endpoints.allMetricsEndpoint(symbol),
                OnChainMetricsDto.class
        );
    }

    @Override
    public OnChainSentimentDto getSentimentFromMetrics(String symbol) {
        return client.get(
                endpoints.sentimentIndicatorEndpoint(symbol),
                OnChainSentimentDto.class
        );
    }

    @Override
    public EstimateNewsDto estimateNews(String symbol) {
        return client.get(
                endpoints.estimateNewsEndpoint(symbol),
                EstimateNewsDto.class
        );
    }

    @Override
    public ExchangeFlowDto getExchangeFlows(String symbol) {
        return client.get(
                endpoints.exchangeFlowEndpoint(symbol),
                ExchangeFlowDto.class
        );
    }

    @Override
    public List<WhaleMovementDto> getWhaleMovements() {
        return client.get(
                endpoints.whaleMovementsEndpoint(),
                new ParameterizedTypeReference<>() {}
        );
    }
}