package com.svsbrains.svscrypto.web.controler;

import com.svsbrains.svscrypto.model.DailyData;
import com.svsbrains.svscrypto.model.MarketData;
import com.svsbrains.svscrypto.model.User;
import com.svsbrains.svscrypto.service.CoinService;
import com.svsbrains.svscrypto.service.DailyDataService;
import com.svsbrains.svscrypto.service.MarketDataService;
import jakarta.servlet.http.HttpSession;
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
    private final CoinService coinService;

    public DetailedViewController(DailyDataService dailyDataService, MarketDataService marketDataService,CoinService coinService) {
        this.dailyDataService = dailyDataService;
        this.marketDataService = marketDataService;
        this.coinService=coinService;
    }

    @GetMapping("/{symbol}")
    public String getCompareCrypto(@PathVariable String symbol, Model model, HttpSession httpSession) {
        DailyData coin = dailyDataService.getBySymbol(symbol).get();

        //TODO: Refactor this
        double monthlyChange = dailyDataService.getMonthlyChange(coin.getSymbol());
        MarketData weekBefore = marketDataService.getKDaysDataOfSymbol(coin.getSymbol(), 7).getLast();
        double weeklyChange = dailyDataService.getChangeFromMarketData(coin.getSymbol(), weekBefore);
        coin.setMonthlyChange(monthlyChange);
        coin.setWeeklyChange(weeklyChange);

        addCoinStatsToModel(symbol, model);

        User u=(User)httpSession.getAttribute("user");
        model.addAttribute("user",u);

        if(u!=null){
            u.addHistoryCoin(symbol);
        }

        List<String> symbols = coinService.getAllSymbols();
        model.addAttribute("symbols",symbols);
        model.addAttribute("pageTitle", symbol);
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

        List<String> symbols = coinService.getAllSymbols();
        model.addAttribute("symbols",symbols);

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
    public Map<String, Object> getPlotData(@PathVariable String symbol, @PathVariable String time, @RequestParam(defaultValue = "open") String field) {
        List<MarketData> data;
        if(time.equals("max")) {
            data = marketDataService.getBySymbol(symbol);
        } else {
            data = marketDataService.getKDaysDataOfSymbol(symbol, Integer.parseInt(time));
        }

        List<Long> timestamps = data.stream().map(MarketData::getTimestamp).toList();
        List<Double> values = data.stream().map(md -> switch(field) {
            case "open" -> md.getOpen();
            case "high" -> md.getHigh();
            case "low" -> md.getLow();
            case "close" -> md.getClose();
            case "volume" -> md.getVolume();
            default -> md.getOpen();
        }).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("timestamps", timestamps);
        response.put("values", values);
        return response;
    }
}
