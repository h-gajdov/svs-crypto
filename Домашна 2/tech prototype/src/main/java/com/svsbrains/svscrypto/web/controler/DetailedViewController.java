package com.svsbrains.svscrypto.web.controler;

import com.svsbrains.svscrypto.model.DailyData;
import com.svsbrains.svscrypto.model.MarketData;
import com.svsbrains.svscrypto.service.DailyDataService;
import com.svsbrains.svscrypto.service.MarketDataService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/details")
public class DetailedViewController {
    private final DailyDataService dailyDataService;
    private final MarketDataService marketDataService;

    public DetailedViewController(DailyDataService dailyDataService, MarketDataService marketDataService) {
        this.dailyDataService = dailyDataService;
        this.marketDataService = marketDataService;
    }

    @GetMapping("/{symbol}")
    public String getCompareCrypto(@PathVariable String symbol, Model model) {
        DailyData coin = dailyDataService.getBySymbol(symbol).get();

        //TODO: Refactor this
        double monthlyChange = dailyDataService.getMonthlyChange(coin.getSymbol());
        MarketData weekBefore = marketDataService.getKDaysDataOfSymbol(coin.getSymbol(), 7).getLast();
        double weeklyChange = dailyDataService.getChangeFromMarketData(coin.getSymbol(), weekBefore);
        coin.setMonthlyChange(monthlyChange);
        coin.setWeeklyChange(weeklyChange);

        addCoinStatsToModel(symbol, model);
        return "master-template";
    }

    private void addCoinStatsToModel(String symbol, Model model) {
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

        model.addAttribute("timestamps", timestamps);
        model.addAttribute("values", values);
        model.addAttribute("rank", dailyDataService.getRankOfSymbol(symbol));
        model.addAttribute("coin", dailyData);
        model.addAttribute("allTimeHigh", allTimeHigh);
        model.addAttribute("allTimeLow", allTimeLow);
        model.addAttribute("allTimeLowChange", allTimeLowChange);
        model.addAttribute("allTimeHighChange", allTimeHighChange);
        model.addAttribute("allTimeLowChangeFormatted", DailyData.formatNumber(allTimeLowChange));
        model.addAttribute("allTimeHighChangeFormatted", DailyData.formatNumber(allTimeHighChange));
        model.addAttribute("bodyContent", "detailed-coin-view");
    }

    @GetMapping("/{symbol}/plot/{time}")
    @ResponseBody
    public Map<String, Object> getPlotData(@PathVariable String symbol, @PathVariable String time) {
        List<MarketData> data;
        if(time.equals("max")) {
            data = marketDataService.getBySymbol(symbol);
        } else {
            data = marketDataService.getKDaysDataOfSymbol(symbol, Integer.parseInt(time));
        }

        List<Long> timestamps = data.stream().map(MarketData::getTimestamp).toList();
        List<Double> values = data.stream().map(MarketData::getOpen).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("timestamps", timestamps);
        response.put("values", values);
        return response;
    }
}
