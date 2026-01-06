package com.svsbrains.svscrypto.web.controler;

import com.svsbrains.svscrypto.model.DailyData;
import com.svsbrains.svscrypto.model.MarketData;
import com.svsbrains.svscrypto.model.User;
import com.svsbrains.svscrypto.service.DailyDataService;
import com.svsbrains.svscrypto.service.MarketDataService;
import com.svsbrains.svscrypto.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/historical-data")
public class HistoricalDataController {
    private final DailyDataService dailyDataService;
    private final MarketDataService marketDataService;
    private final UserService userService;

    public HistoricalDataController(DailyDataService dailyDataService, MarketDataService marketDataService, UserService userService) {
        this.dailyDataService = dailyDataService;
        this.marketDataService = marketDataService;
        this.userService = userService;
    }

    @GetMapping
    public String getHistory(Model model,
                             @AuthenticationPrincipal UserDetails userDetails){
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("bodyContent","history");

        List<DailyData> topPrice = userService.getSearchHistory(user.getUsername());

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

        model.addAttribute("user", user);
        model.addAttribute("tableCoins", topPrice);
        model.addAttribute("sparklineData", sparklineData);
        model.addAttribute("pageTitle", "Search history");
        return "master-template";
    }
}
