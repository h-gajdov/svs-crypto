package com.svsbrains.svscrypto.service.impl;

import com.svsbrains.svscrypto.client.AnalysisEngineClient;
import com.svsbrains.svscrypto.client.AnalysisEngineEndpoints;
import com.svsbrains.svscrypto.model.dto.SymbolIndicatorsDto;
import com.svsbrains.svscrypto.service.IndicatorsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service implementation responsible for retrieving technical indicators
 * for a given cryptocurrency symbol.
 * <p>
 * This service acts as an intermediary between the application and the
 * external Analysis Engine microservice. It delegates HTTP communication
 * to {@link AnalysisEngineClient} and uses {@link AnalysisEngineEndpoints}
 * to construct the appropriate endpoint URLs.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class IndicatorServiceImpl implements IndicatorsService {
    private final AnalysisEngineClient client;
    private final AnalysisEngineEndpoints endpoints;

    /**
     * Retrieves calculated indicators for the specified cryptocurrency symbol.
     *
     * @param symbol the trading symbol (e.g. BTC, ETH)
     * @return a {@link SymbolIndicatorsDto} containing the indicator values,
     *         or {@code null} if the Analysis Engine service is unavailable
     */
    @Override
    public SymbolIndicatorsDto getIndicators(String symbol) {
        return client.get(
                endpoints.indicatorsEndpoint(symbol),
                SymbolIndicatorsDto.class
        );
    }
}