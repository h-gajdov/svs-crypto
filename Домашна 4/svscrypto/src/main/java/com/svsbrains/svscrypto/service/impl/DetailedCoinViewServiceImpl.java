package com.svsbrains.svscrypto.service.impl;

import com.svsbrains.svscrypto.model.DailyData;
import com.svsbrains.svscrypto.model.MarketData;
import com.svsbrains.svscrypto.service.CoinService;
import com.svsbrains.svscrypto.service.DailyDataService;
import com.svsbrains.svscrypto.service.DetailedCoinViewService;
import com.svsbrains.svscrypto.service.MarketDataService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DetailedCoinViewServiceImpl implements DetailedCoinViewService {

    private final MarketDataService marketDataService;
    private final DailyDataService dailyDataService;
    private final CoinService coinService;

    public DetailedCoinViewServiceImpl(MarketDataService marketDataService, DailyDataService dailyDataService, CoinService coinService) {
        this.marketDataService = marketDataService;
        this.dailyDataService = dailyDataService;
        this.coinService = coinService;
    }

    private void enrichWithChanges(DailyData coin) {
        double monthlyChange = dailyDataService.getMonthlyChange(coin.getSymbol());
        MarketData weekBefore = marketDataService.getKDaysDataOfSymbol(coin.getSymbol(), 7).getLast();
        double weeklyChange = dailyDataService.getChangeFromMarketData(coin.getSymbol(), weekBefore);
        coin.setMonthlyChange(monthlyChange);
        coin.setWeeklyChange(weeklyChange);
    }

    @Override
    public Map<String, Object> buildDetailedView(String symbol) {
        DailyData dailyDataCoin = dailyDataService.getBySymbol(symbol);

        enrichWithChanges(dailyDataCoin);

        Map<String, Object> model = new HashMap<>();

        MarketData allTimeLow = marketDataService.getAllTimeLow(symbol);
        MarketData allTimeHigh = marketDataService.getAllTimeHigh(symbol);

        double allTimeLowChange = ((dailyDataCoin.getLast_price() - allTimeLow.getLow()) / allTimeLow.getLow()) * 100;
        double allTimeHighChange = ((dailyDataCoin.getLast_price() - allTimeHigh.getHigh()) / allTimeHigh.getHigh()) * 100;

        List<MarketData> data = marketDataService.getBySymbol(symbol);

        List<Long> timestamps = data.stream()
                .map(MarketData::getTimestamp)
                .toList();
        List<Double> values = data.stream()
                .map(MarketData::getOpen)
                .toList();

        model.put("symbols", coinService.getAllSymbols());
        model.put("timestamps", timestamps);
        model.put("values", values);
        model.put("rank", dailyDataService.getRankOfSymbol(symbol));
        model.put("coin", dailyDataCoin);
        model.put("allTimeHigh", allTimeHigh);
        model.put("allTimeLow", allTimeLow);
        model.put("allTimeLowChange", allTimeLowChange);
        model.put("allTimeHighChange", allTimeHighChange);
        model.put("allTimeLowChangeFormatted", DailyData.formatNumber(allTimeLowChange));
        model.put("allTimeHighChangeFormatted", DailyData.formatNumber(allTimeHighChange));
        model.put("bodyContent", "detailed-coin-view");

        return model;
    }

    @Override
    public Map<String, Object> getPlotData(String symbol, String time, String field) {
        List<MarketData> data = fetchMarketData(symbol, time);

        Map<String, Object> response = new HashMap<>();
        response.put("timestamps", extractTimestamps(data));

        if ("candles".equalsIgnoreCase(field)) {
            response.putAll(extractOHLCV(data));
        } else {
            response.put("values", extractSingleField(data, field));
        }

        return response;
    }

    /**
     * Fetches market data for a given cryptocurrency symbol and timeframe.
     *
     * @param symbol the symbol of the cryptocurrency (e.g., "BTC")
     * @param time   the timeframe to fetch ("max" for all data or number of days as a string)
     * @return a list of {@link MarketData} for the given symbol and timeframe
     */
    private List<MarketData> fetchMarketData(String symbol, String time) {
        if (time.equalsIgnoreCase("max")) {
            return marketDataService.getBySymbol(symbol);
        }
        return marketDataService.getKDaysDataOfSymbol(symbol, Integer.parseInt(time));
    }

    /**
     * Extracts the timestamps from a list of MarketData.
     *
     * @param data a list of {@link MarketData} objects
     * @return a list of timestamps (in seconds since epoch) corresponding to each data point
     */
    private List<Long> extractTimestamps(List<MarketData> data) {
        return data.stream().map(MarketData::getTimestamp).toList();
    }

    /**
     * Extracts OHLCV (Open, High, Low, Close, Volume) data from a list of MarketData.
     *
     * @param data a list of {@link MarketData} objects
     * @return a map containing the OHLCV data, with keys:
     *         <ul>
     *           <li>"open"  -> list of opening prices</li>
     *           <li>"high"  -> list of high prices</li>
     *           <li>"low"   -> list of low prices</li>
     *           <li>"close" -> list of closing prices</li>
     *           <li>"volume"-> list of volumes</li>
     *         </ul>
     */
    private Map<String, List<Double>> extractOHLCV(List<MarketData> data) {
        Map<String, List<Double>> ohlcv = new HashMap<>();
        ohlcv.put("open", data.stream().map(MarketData::getOpen).toList());
        ohlcv.put("high", data.stream().map(MarketData::getHigh).toList());
        ohlcv.put("low", data.stream().map(MarketData::getLow).toList());
        ohlcv.put("close", data.stream().map(MarketData::getClose).toList());
        ohlcv.put("volume", data.stream().map(MarketData::getVolume).toList());
        return ohlcv;
    }

    /**
     * Extracts a single numeric field from a list of MarketData.
     *
     * @param data  a list of {@link MarketData} objects
     * @param field the field to extract; possible values: "open", "high", "low", "close", "volume", "price"
     * @return a list of double values corresponding to the specified field
     *         <p>
     *         "price" is treated as the opening price by default.
     *         Any unrecognized field will default to "open".
     *         </p>
     */
    private List<Double> extractSingleField(List<MarketData> data, String field) {
        return data.stream().map(md -> switch (field.toLowerCase()) {
            case "high" -> md.getHigh();
            case "low" -> md.getLow();
            case "close" -> md.getClose();
            case "volume" -> md.getVolume();
            default -> md.getOpen();
        }).toList();
    }
}
