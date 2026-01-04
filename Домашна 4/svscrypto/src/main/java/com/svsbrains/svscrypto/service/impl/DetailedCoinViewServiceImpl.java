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
        DailyData dailyDataCoin = dailyDataService.getBySymbol(symbol)
                .orElseThrow(() -> new IllegalArgumentException("Coin not found"));

        enrichWithChanges(dailyDataCoin);

        Map<String, Object> model=new HashMap<>();

        MarketData allTimeLow = marketDataService.getAllTimeLow(symbol).get();
        MarketData allTimeHigh = marketDataService.getAllTimeHigh(symbol).get();
        DailyData dailyData = dailyDataService.getBySymbol(symbol).get();

        double allTimeLowChange = ((dailyData.getLast_price() - allTimeLow.getLow()) / allTimeLow.getLow()) * 100;
        double allTimeHighChange = ((dailyData.getLast_price() - allTimeHigh.getHigh()) / allTimeHigh.getHigh()) * 100;

        List<MarketData> data = marketDataService.getBySymbol(symbol);

        List<Long> timestamps = data.stream()
                .map(MarketData::getTimestamp)
                .toList();
        List<Double> values = data.stream()
                .map(MarketData::getOpen)
                .toList();

        model.put("symbols",coinService.getAllSymbols());
        model.put("timestamps", timestamps);
        model.put("values", values);
        model.put("rank", dailyDataService.getRankOfSymbol(symbol));
        model.put("coin", dailyData);
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
        List<MarketData> data;
        if(time.equals("max")) {
            data = marketDataService.getBySymbol(symbol);
        } else {
            data = marketDataService.getKDaysDataOfSymbol(symbol, Integer.parseInt(time));
        }

        List<Long> timestamps = data.stream().map(MarketData::getTimestamp).toList();
        List<Double> values = data.stream().map(md ->extractField(md,field)).toList();

        Map<String, Object> response = new HashMap<>();

        response.put("timestamps", timestamps);
        response.put("values", values);

        return response;
    }

    private double extractField(MarketData md, String field) {
        return switch (field) {
            case "high" -> md.getHigh();
            case "low" -> md.getLow();
            case "close" -> md.getClose();
            case "volume" -> md.getVolume();
            default -> md.getOpen();
        };
    }
}
