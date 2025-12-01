package com.svsbrains.svscrypto.web.controler;

import com.svsbrains.svscrypto.model.DailyData;
import com.svsbrains.svscrypto.model.MarketData;
import com.svsbrains.svscrypto.service.DailyDataService;
import com.svsbrains.svscrypto.service.MarketDataService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
        addCoinStatsToModel(symbol, model); // common stats for the coin
        return "master-template";
    }

    // Separate POST mapping to prepare chart data
    @PostMapping("/{symbol}/plot/{time}")
    public String plotTimeframe(@PathVariable String symbol,
                                @PathVariable String time,
                                Model model) {

        addCoinStatsToModel(symbol, model); // include coin stats
        addPlotDataToModel(symbol, time, model); // only plot data

        return "master-template"; // render same template with updated chart
    }

    // ---------------- Helper functions ----------------

    // Add common coin stats to the model
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

    private void addPlotDataToModel(String symbol, String time, Model model) {
        int days;
        switch (time.toLowerCase()) {
            case "7d": days = 7; break;
            case "30d": days = 30; break;
            case "90d": days = 90; break;
            default: days = 30;
        }

        List<MarketData> data = marketDataService.getKDaysDataOfSymbol(symbol, days);

        List<Long> timestamps = data.stream()
                .map(MarketData::getTimestamp)
                .toList();
        List<Double> values = data.stream()
                .map(MarketData::getOpen)
                .toList();

        model.addAttribute("timestamps", timestamps);
        model.addAttribute("values", values);
    }
}
