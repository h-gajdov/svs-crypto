package com.svsbrains.svscrypto.service.impl;

import com.svsbrains.svscrypto.client.AnalysisEngineClient;
import com.svsbrains.svscrypto.client.AnalysisEngineEndpoints;
import com.svsbrains.svscrypto.model.dto.*;
import com.svsbrains.svscrypto.service.OnChainService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation responsible for retrieving on-chain metrics
 * and related analytical data for cryptocurrencies.
 * <p>
 * This service delegates data retrieval to the external Analysis Service
 * microservice via {@link AnalysisEngineClient}. Endpoint URLs are constructed
 * using {@link AnalysisEngineEndpoints}, keeping communication logic separated
 * from business logic.
 * </p>
 * <p>
 * The service provides access to multiple on-chain data types, including
 * raw metrics, sentiment indicators, news estimation, exchange flows,
 * and whale movement activity.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class OnChainMetricsServiceImpl implements OnChainService {
    private final AnalysisEngineClient client;
    private final AnalysisEngineEndpoints endpoints;

    /**
     * Retrieves all available on-chain metrics for the specified symbol.
     *
     * @param symbol the cryptocurrency symbol (e.g. BTC, ETH)
     * @return an {@link OnChainMetricsDto} containing aggregated on-chain data,
     *         or {@code null} if the Analysis Engine service is unavailable
     */
    @Override
    public OnChainMetricsDto getAllMetrics(String symbol) {
        return client.get(
                endpoints.allMetricsEndpoint(symbol),
                OnChainMetricsDto.class
        );
    }

    /**
     * Retrieves sentiment indicators derived from on-chain metrics.
     *
     * @param symbol the cryptocurrency symbol
     * @return an {@link OnChainSentimentDto} containing sentiment analysis results,
     *         or {@code null} if the Analysis Engine service is unavailable
     */
    @Override
    public OnChainSentimentDto getSentimentFromMetrics(String symbol) {
        return client.get(
                endpoints.sentimentIndicatorEndpoint(symbol),
                OnChainSentimentDto.class
        );
    }

    /**
     * Estimates news impact or sentiment for the specified cryptocurrency symbol.
     *
     * @param symbol the cryptocurrency symbol
     * @return an {@link EstimateNewsDto} containing the estimated news analysis,
     *         or {@code null} if the Analysis Engine service is unavailable
     */
    @Override
    public EstimateNewsDto estimateNews(String symbol) {
        return client.get(
                endpoints.estimateNewsEndpoint(symbol),
                EstimateNewsDto.class
        );
    }

    /**
     * Retrieves exchange inflow and outflow data for the specified symbol.
     *
     * @param symbol the cryptocurrency symbol
     * @return an {@link ExchangeFlowDto} containing exchange flow information,
     *         or {@code null} if the Analysis Engine service is unavailable
     */
    @Override
    public ExchangeFlowDto getExchangeFlows(String symbol) {
        return client.get(
                endpoints.exchangeFlowEndpoint(symbol),
                ExchangeFlowDto.class
        );
    }

    /**
     * Retrieves recent whale movement activity across supported cryptocurrencies.
     *
     * @return a list of {@link WhaleMovementDto} entries representing large on-chain movements,
     *         or {@code null} if the Analysis Engine service is unavailable
     */
    @Override
    public List<WhaleMovementDto> getWhaleMovements() {
        return client.get(
                endpoints.whaleMovementsEndpoint(),
                new ParameterizedTypeReference<>() {}
        );
    }
}