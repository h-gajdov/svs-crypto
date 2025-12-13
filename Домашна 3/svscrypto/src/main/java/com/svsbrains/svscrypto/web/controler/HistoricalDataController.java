package com.svsbrains.svscrypto.web.controler;

import com.svsbrains.svscrypto.model.DailyData;
import com.svsbrains.svscrypto.model.MarketData;
import com.svsbrains.svscrypto.model.User;
import com.svsbrains.svscrypto.service.CoinService;
import com.svsbrains.svscrypto.service.DailyDataService;
import com.svsbrains.svscrypto.service.MarketDataService;
import com.svsbrains.svscrypto.service.UserService;
import jakarta.servlet.http.HttpSession;
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
    private final UserService userService;
    private final HttpSession httpSession;
    private final DailyDataService dailyDataService;
    private final MarketDataService marketDataService;
    private final CoinService coinService;

    public HistoricalDataController(UserService userService, HttpSession httpSession, DailyDataService dailyDataService,CoinService coinService, MarketDataService marketDataService) {
        this.userService = userService;
        this.httpSession = httpSession;
        this.dailyDataService = dailyDataService;
        this.marketDataService = marketDataService;
        this.coinService=coinService;
    }

    @GetMapping
    public String getHistory(Model model){
        User u=(User)httpSession.getAttribute("user");
        if(u==null) return "redirect:/login";

        model.addAttribute("bodyContent","history");

        List<DailyData> topPrice = u.getHistoryCoins().stream().map(symbol->dailyDataService.getBySymbol(symbol).orElse(null)).toList();

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

        List<String> symbols = coinService.getAllSymbols();
        model.addAttribute("symbols",symbols);

        model.addAttribute("user",httpSession.getAttribute("user"));
        model.addAttribute("tableCoins", topPrice);
        model.addAttribute("sparklineData", sparklineData);

        return "master-template";
    }
}
