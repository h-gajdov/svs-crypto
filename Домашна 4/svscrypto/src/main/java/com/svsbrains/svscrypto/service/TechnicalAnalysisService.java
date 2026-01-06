package com.svsbrains.svscrypto.service;

import com.svsbrains.svscrypto.model.dto.SymbolAnalysisResponse;

/**
 * Service responsible for providing technical analysis
 * data for cryptocurrency symbols.
 */
public interface TechnicalAnalysisService {

    /**
     * Retrieves technical analysis data for a given cryptocurrency symbol.
     * <p>
     * The returned response contains indicator-based signals
     * (e.g. RSI, MACD, SMA) across multiple timeframes
     * daily, weekly, and monthly.
     *
     * @param symbol cryptocurrency symbol (e.g. BTC, ETH)
     * @return {@link SymbolAnalysisResponse} containing technical signals
     *         for the specified symbol
     * @throws RuntimeException if the external analysis engine
     *         is unavailable or returns an invalid response
     */
    SymbolAnalysisResponse getTechnicalAnalysis(String symbol);
}
