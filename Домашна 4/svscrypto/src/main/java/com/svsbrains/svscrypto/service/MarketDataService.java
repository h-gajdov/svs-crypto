package com.svsbrains.svscrypto.service;

import com.svsbrains.svscrypto.model.MarketData;

import java.util.List;

/**
 * Service for accessing and managing market data of cryptocurrencies.
 * <p>
 * This interface defines operations to retrieve historical market data, recent prices,
 * and all-time high/low values for one or multiple symbols. Implementations
 * should handle data fetching, filtering, and any necessary business logic.
 * </p>
 */
public interface MarketDataService {

    /**
     * Retrieves all market data records for a given cryptocurrency symbol.
     *
     * @param symbol the unique symbol of the cryptocurrency (e.g., BTC, ETH)
     * @return a list of {@link MarketData} associated with the symbol, ordered by timestamp
     */
    List<MarketData> getBySymbol(String symbol);

    /**
     * Retrieves the earliest market data record for each cryptocurrency symbol.
     * <p>
     * Useful for identifying when a coin first appeared on the market.
     * </p>
     *
     * @return a list of {@link MarketData}, each representing the first timestamp of a coin
     */
    List<MarketData> getAllFirstTimestamp();

    /**
     * Retrieves the last {@code k} days of market data for a given cryptocurrency symbol.
     * <p>
     * Typically used for charts, sparklines, or short-term trend analysis.
     * </p>
     *
     * @param symbol the cryptocurrency symbol
     * @param k      the number of most recent days to retrieve
     * @return a list of {@link MarketData} limited to the last {@code k} days
     */
    List<MarketData> getKDaysDataOfSymbol(String symbol, int k);

    /**
     * Retrieves the market data record representing the all-time high price of a cryptocurrency.
     *
     * @param symbol the cryptocurrency symbol
     * @return the {@link MarketData} record with the highest price for the given symbol
     * @throws com.svsbrains.svscrypto.model.exceptions.MarketDataNotFoundException if no data is found
     */
    MarketData getAllTimeHigh(String symbol);

    /**
     * Retrieves the market data record representing the all-time low price of a cryptocurrency.
     *
     * @param symbol the cryptocurrency symbol
     * @return the {@link MarketData} record with the lowest price for the given symbol
     * @throws com.svsbrains.svscrypto.model.exceptions.MarketDataNotFoundException if no data is found
     */
    MarketData getAllTimeLow(String symbol);

    /**
     * Retrieves market data for multiple cryptocurrency symbols within the last {@code days}.
     * <p>
     * Each inner list corresponds to one symbol in the order provided. Only records
     * whose timestamp falls within the last {@code days} from the current date are included.
     * </p>
     *
     * @param symbols a list of cryptocurrency symbols
     * @param days    the number of most recent days to include
     * @return a list of lists of {@link MarketData}, where each inner list contains
     *         market data for a single symbol
     */
    List<List<MarketData>> getMarketDataForSymbols(List<String> symbols, int days);
}