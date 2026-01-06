package com.svsbrains.svscrypto.service;

import com.svsbrains.svscrypto.model.DailyData;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * Service for providing dashboard-related data.
 * <p>
 * This interface defines operations for retrieving top cryptocurrencies based on various metrics,
 * as well as preparing sparkline (price history) data for display in the dashboard.
 * </p>
 */
public interface DashboardService {

    /**
     * Retrieves a paginated list of {@link DailyData} ordered by market capitalization.
     * <p>
     * Each {@link DailyData} object may be enriched with additional computed metrics such as
     * monthly or three-month price changes.
     * </p>
     *
     * @param page the page number (0-based)
     * @param size the number of items per page
     * @return a {@link Page} of {@link DailyData} sorted by market capitalization
     */
    Page<DailyData> getDashboardMarketCap(int page, int size);

    /**
     * Retrieves the top cryptocurrencies ordered by price.
     *
     * @return a list of {@link DailyData} representing the top-priced coins
     */
    List<DailyData> getTopByPrice();

    /**
     * Retrieves the top cryptocurrencies ordered by trading volume.
     *
     * @return a list of {@link DailyData} representing the highest-volume coins
     */
    List<DailyData> getTopByVolume();

    /**
     * Retrieves the top cryptocurrencies with the highest gains.
     *
     * @return a list of {@link DailyData} representing the top gainers
     */
    List<DailyData> getTopByGain();

    /**
     * Retrieves the newest cryptocurrencies based on their first appearance in the market.
     *
     * @return a list of {@link DailyData} representing the newest coins
     */
    List<DailyData> getTopNew();

    /**
     * Generates sparkline data for a collection of coins.
     * <p>
     * For each coin in the input {@link Page}, this method retrieves the historical price
     * for the last few days and returns it as a map.
     * </p>
     *
     * @param coins a page of {@link DailyData} for which sparkline data is requested
     * @return a map where each key is a coin symbol and the value is a list of historical prices
     */
    Map<String, List<Double>> getSparklineData(Page<DailyData> coins);
}
