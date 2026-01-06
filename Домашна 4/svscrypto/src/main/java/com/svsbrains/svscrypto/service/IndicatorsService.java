package com.svsbrains.svscrypto.service;

import com.svsbrains.svscrypto.client.AnalysisEngineClient;
import com.svsbrains.svscrypto.client.AnalysisEngineEndpoints;
import com.svsbrains.svscrypto.model.dto.SymbolIndicatorsDto;

/**
 * Service responsible for retrieving technical indicators
 * for a given cryptocurrency symbol.
 * <p>
 * This service acts as an intermediary between the application and the
 * external Analysis Service microservice. It delegates HTTP communication
 * to {@link AnalysisEngineClient} and uses {@link AnalysisEngineEndpoints}
 * to construct the appropriate endpoint URLs.
 * </p>
 */
public interface IndicatorsService {
    /**
     * Retrieves calculated indicators for the specified cryptocurrency symbol.
     *
     * @param symbol the trading symbol (e.g. BTC, ETH)
     * @return a {@link SymbolIndicatorsDto} containing the indicator values,
     *         or {@code null} if the Analysis Engine service is unavailable
     */
    SymbolIndicatorsDto getIndicators(String symbol);
}
