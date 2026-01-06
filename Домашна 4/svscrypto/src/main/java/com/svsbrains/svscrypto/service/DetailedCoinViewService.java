package com.svsbrains.svscrypto.service;

import com.svsbrains.svscrypto.model.User;

import java.util.List;
import java.util.Map;

/**
 * Service interface for building detailed views and chart data for a single cryptocurrency.
 * <p>
 * This service provides methods to prepare comprehensive information about a coin,
 * including historical price data, all-time high/low metrics, rank, and short-term
 * and long-term changes. The resulting data is typically used to render detailed
 * coin views in the dashboard.
 * </p>
 */
public interface DetailedCoinViewService {

    /**
     * Builds a detailed view model for a given cryptocurrency symbol.
     * <p>
     * The returned map contains:
     * <ul>
     *     <li>Coin data ({@link com.svsbrains.svscrypto.model.DailyData})</li>
     *     <li>All-time high and low values</li>
     *     <li>Percentage changes relative to all-time high and low</li>
     *     <li>Weekly and monthly price changes</li>
     *     <li>Historical timestamps and price values for plotting charts</li>
     *     <li>List of all available symbols for selection</li>
     *     <li>Rank of the coin among all tracked cryptocurrencies</li>
     * </ul>
     * </p>
     *
     * @param symbol the cryptocurrency symbol (e.g., "BTC", "ETH")
     * @return a map of key-value pairs representing the detailed coin view data
     */
    Map<String, Object> buildDetailedView(String symbol);

    /**
     * Generates plot data for a cryptocurrency over a specified period and field.
     * <p>
     * The returned map contains two lists:
     * <ul>
     *     <li>"timestamps": a list of epoch timestamps for the data points</li>
     *     <li>"values": a list of numerical values corresponding to the selected field</li>
     * </ul>
     * The field parameter determines which value to extract from the market data, such as:
     * "open", "high", "low", "close", or "volume". The time parameter can be either a
     * number of days (e.g., "7") or "max" to include all available data.
     * </p>
     *
     * @param symbol the cryptocurrency symbol
     * @param time   the period for which to retrieve data (number of days or "max")
     * @param field  the market data field to extract ("open", "high", "low", "close", "volume")
     * @return a map containing timestamps and corresponding field values for plotting
     */
    Map<String, Object> getPlotData(String symbol, String time, String field);
}