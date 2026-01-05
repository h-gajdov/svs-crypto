package com.svsbrains.svscrypto.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Provides fully-qualified URLs for all endpoints exposed by the Analysis Service.
 * <p>
 * This class acts as a central point for managing and constructing URLs for various analysis endpoints,
 * so that other services in the application can access the Analysis Service.
 * </p>
 * <p>
 * The base URL of the analysis engine is injected via the application properties:
 * <code>analysis.engine.base-url</code>
 * </p>
 */
@Component
public class AnalysisEngineEndpoints {

    /** Endpoint path for retrieving all on-chain metrics for a symbol */
    private static final String GET_ALL_METRICS = "/metrics";

    /** Endpoint path for retrieving on-chain sentiment indicator for a symbol */
    private static final String GET_SENTIMENT_INDICATOR = "/get-indicator-onchain";

    /** Endpoint path for estimating news sentiment for a symbol */
    private static final String GET_ESTIMATE_NEWS = "/estimate-news";

    /** Endpoint path for retrieving whale movements */
    private static final String GET_WHALE_MOVEMENTS = "/whale-movements";

    /** Endpoint path for retrieving exchange flow for a symbol */
    private static final String GET_EXCHANGE_FLOW = "/exchange-flow";

    /** Endpoint path for retrieving additional indicators for a symbol */
    private static final String GET_INDICATORS = "/identificators";

    /** Endpoint path for retrieving LSTM price prediction for a symbol */
    private static final String GET_LSTM_PREDICTION = "/api/predict";

    /** Base URL for the Analysis Engine, injected from application properties */
    @Value("${analysis.engine.base-url}")
    private String baseUrl;

    /**
     * Combines a given endpoint path with a symbol to form a full URL.
     * The symbol is automatically converted to uppercase.
     *
     * @param endpoint the endpoint path (e.g., "/metrics")
     * @param symbol   the symbol to append to the URL (e.g., "BTC")
     * @return the full URL to call the endpoint for the given symbol
     */
    private String combineEndpointWithSymbol(String endpoint, String symbol) {
        return baseUrl + endpoint + "/" + symbol.toUpperCase();
    }

    /**
     * Returns the full URL for retrieving all metrics for a symbol.
     *
     * @param symbol the symbol (e.g., "BTC")
     * @return full URL to call the metrics endpoint
     */
    public String allMetricsEndpoint(String symbol) {
        return combineEndpointWithSymbol(GET_ALL_METRICS, symbol);
    }

    /**
     * Returns the full URL for retrieving the on-chain sentiment indicator for a symbol.
     *
     * @param symbol the symbol
     * @return full URL to call the sentiment indicator endpoint
     */
    public String sentimentIndicatorEndpoint(String symbol) {
        return combineEndpointWithSymbol(GET_SENTIMENT_INDICATOR, symbol);
    }

    /**
     * Returns the full URL for estimating news sentiment for a symbol.
     *
     * @param symbol the symbol
     * @return full URL to call the news sentiment endpoint
     */
    public String estimateNewsEndpoint(String symbol) {
        return combineEndpointWithSymbol(GET_ESTIMATE_NEWS, symbol);
    }

    /**
     * Returns the full URL for retrieving whale movements.
     *
     * @return full URL to call the whale movements endpoint
     */
    public String whaleMovementsEndpoint() {
        return baseUrl + GET_WHALE_MOVEMENTS;
    }

    /**
     * Returns the full URL for retrieving exchange flow for a symbol.
     *
     * @param symbol the symbol
     * @return full URL to call the exchange flow endpoint
     */
    public String exchangeFlowEndpoint(String symbol) {
        return combineEndpointWithSymbol(GET_EXCHANGE_FLOW, symbol);
    }

    /**
     * Returns the full URL for retrieving additional indicators for a symbol.
     *
     * @param symbol the symbol
     * @return full URL to call the indicators endpoint
     */
    public String indicatorsEndpoint(String symbol) {
        return combineEndpointWithSymbol(GET_INDICATORS, symbol);
    }

    /**
     * Returns the full URL for retrieving LSTM price prediction for a symbol.
     *
     * @param symbol the symbol
     * @return full URL to call the prediction endpoint
     */
    public String predictionEndpoint(String symbol) {
        return combineEndpointWithSymbol(GET_LSTM_PREDICTION, symbol);
    }
}