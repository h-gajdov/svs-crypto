package com.svsbrains.svscrypto.web.controler;

import com.svsbrains.svscrypto.model.DailyData;
import com.svsbrains.svscrypto.model.MarketData;
import com.svsbrains.svscrypto.service.DailyDataService;
import com.svsbrains.svscrypto.service.MarketDataService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.yaml.snakeyaml.error.Mark;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    private final MarketDataService marketDataService;
    private final DailyDataService dailyDataService;

    public DashboardController(MarketDataService marketDataService, DailyDataService dailyDataService) throws IOException {
        this.marketDataService = marketDataService;
        this.dailyDataService = dailyDataService;
    }

    @GetMapping
    public String getDashboard(Model model) {
        model.addAttribute("bodyContent", "dashboard");
        List<DailyData> topPrice = dailyDataService.getTopByPrice(10);
        List<DailyData> top3Gain = dailyDataService.getTopByGain(3);
        List<DailyData> top3New = dailyDataService.getTopNew(3);
        List<DailyData> top3Volume = dailyDataService.getTopByVolume(3);

        //TODO: Refactor this
        topPrice.forEach(coin -> {
            double monthlyChange = dailyDataService.getMonthlyChange(coin.getSymbol());
            MarketData threeMonthsBefore = marketDataService.getKDaysDataOfSymbol(coin.getSymbol(), 90).getLast();
            double threeMonthsChange = dailyDataService.getChangeFromMarketData(coin.getSymbol(), threeMonthsBefore);
            coin.setMonthlyChange(monthlyChange);
            coin.setThreeMonthsChange(threeMonthsChange);
        });

        Map<String, List<Double>> sparklineData = new HashMap<>();
        for (DailyData coin : topPrice) {
            List<MarketData> weekly = marketDataService.getKDaysDataOfSymbol(coin.getSymbol(), 7);
            List<Double> res = weekly.stream().map(MarketData::getOpen).toList();
            sparklineData.put(coin.getSymbol(), res);
        }

        model.addAttribute("top3Price", topPrice.subList(0, 3));
        model.addAttribute("top3Volume", top3Volume);
        model.addAttribute("top3New", top3New);
        model.addAttribute("top3Gain", top3Gain);
        model.addAttribute("tableCoins", topPrice);
        model.addAttribute("sparklineData", sparklineData);
        return "master-template";
    }
}
