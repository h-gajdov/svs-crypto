package com.svsbrains.svscrypto.service;

import com.svsbrains.svscrypto.model.DailyData;
import com.svsbrains.svscrypto.model.MarketData;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service for managing {@link DailyData} entities.
 * <p>
 * This service defines business-level operations for accessing daily cryptocurrency data,
 * such as retrieving top-performing coins, computing price changes, and ranking coins by market metrics.
 * Implementations are responsible for interacting with the persistence layer
 * and any additional business logic.
 * </p>
 */
public interface DailyDataService {
    /**
     * Retrieves the {@link DailyData} for a given cryptocurrency symbol.
     *
     * @param symbol the unique symbol of the cryptocurrency (e.g., BTC, ETH)
     * @return the {@link DailyData} associated with the symbol
     * @throws com.svsbrains.svscrypto.model.exceptions.DailyDataNotFoundException if no data exists for the symbol
     */
    DailyData getBySymbol(String symbol);

    /**
     * Retrieves a paginated list of {@link DailyData} ordered by market capitalization in descending order.
     *
     * @param pageNum  the page number (0-based)
     * @param pageSize the number of items per page
     * @return a {@link Page} of {@link DailyData} sorted by market capitalization
     */
    Page<DailyData> getTopByMarketCap(int pageNum, int pageSize);

    /**
     * Retrieves the top {@code k} cryptocurrencies ordered by price in descending order.
     *
     * @param k the number of top cryptocurrencies to retrieve
     * @return a list of {@link DailyData} objects representing the top-priced coins
     */
    List<DailyData> getTopByPrice(int k);

    /**
     * Retrieves the newest {@code k} cryptocurrencies based on their first timestamp in the market.
     *
     * @param k the number of newest cryptocurrencies to retrieve
     * @return a list of {@link DailyData} objects representing the newest coins
     */
    List<DailyData> getTopNew(int k);

    /**
     * Retrieves the top {@code k} cryptocurrencies with the highest gains.
     *
     * @param k the number of top-gaining cryptocurrencies to retrieve
     * @return a list of {@link DailyData} objects representing the top gainers
     */
    List<DailyData> getTopByGain(int k);

    /**
     * Retrieves the top {@code k} cryptocurrencies with the highest trading volume.
     *
     * @param k the number of top-volume cryptocurrencies to retrieve
     * @return a list of {@link DailyData} objects representing the highest-volume coins
     */
    List<DailyData> getTopByVolume(int k);

    /**
     * Calculates the percentage change of a cryptocurrency over the past month.
     *
     * @param symbol the unique symbol of the cryptocurrency
     * @return the monthly percentage change in price
     */
    double getMonthlyChange(String symbol);

    /**
     * Calculates the percentage change of a cryptocurrency relative to a given {@link MarketData} point.
     *
     * @param symbol     the unique symbol of the cryptocurrency
     * @param marketData the reference {@link MarketData} to compare against
     * @return the percentage change from the reference market data
     */
    double getChangeFromMarketData(String symbol, MarketData marketData);

    /**
     * Retrieves the rank of a cryptocurrency symbol based on its market cap.
     *
     * @param symbol the unique symbol of the cryptocurrency
     * @return the rank of the cryptocurrency (1 = highest market cap)
     */
    int getRankOfSymbol(String symbol);

    /**
     * Populates the given list of {@link DailyData} objects with calculated change values.
     * <p>
     * This method computes relevant price changes (e.g., daily, monthly, or multi-period)
     * for each coin in the list and sets them into the corresponding fields of {@link DailyData}.
     * It is typically used before rendering coin data in dashboards or historical views.
     * </p>
     *
     * @param coins the list of {@link DailyData} objects to update with change information
     */
    void setCoinsChanges(List<DailyData> coins);

    /**
     * Generates sparkline data for a list of cryptocurrencies.
     * <p>
     * A sparkline is a compact representation of a coin's price history,
     * usually a list of opening prices over the past N days.
     * This method returns a map where each key is a coin symbol and each value
     * is a list of doubles representing the historical prices for that coin.
     * </p>
     *
     * @param coins the list of {@link DailyData} objects for which to generate sparkline data
     * @return a {@link Map} where the key is the coin symbol and the value is a list of historical prices
     */
    Map<String, List<Double>> getSparklineData(List<DailyData> coins);

    List<DailyData> getCoinsBySymbols(List<String> symbols);
}
