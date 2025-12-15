package com.svsbrains.svscrypto.web.controler;

import com.svsbrains.svscrypto.model.DailyData;
import com.svsbrains.svscrypto.model.MarketData;
import com.svsbrains.svscrypto.model.User;
import com.svsbrains.svscrypto.service.CoinService;
import com.svsbrains.svscrypto.service.DailyDataService;
import com.svsbrains.svscrypto.service.MarketDataService;
import com.svsbrains.svscrypto.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
//@RequestMapping("/dashboard")
public class DashboardController {

    private final MarketDataService marketDataService;
    private final DailyDataService dailyDataService;
    private final UserService userService;
    private final CoinService coinService;

    public DashboardController(MarketDataService marketDataService, DailyDataService dailyDataService,UserService userService,CoinService coinService) throws IOException {
        this.marketDataService = marketDataService;
        this.dailyDataService = dailyDataService;
        this.userService=userService;
        this.coinService=coinService;
    }

    @GetMapping({"/", "/dashboard"})
    public String getDashboard(Model model, HttpSession httpSession, @RequestParam(required = false) Integer pageNum) {
        model.addAttribute("bodyContent", "dashboard");
        if(pageNum == null || pageNum <= 0) pageNum = 1;
        Page<DailyData> topMarketCap = dailyDataService.getTopByMarketCap(pageNum - 1, 10);
        List<DailyData> top3Price = dailyDataService.getTopByPrice(3);
        List<DailyData> top3Gain = dailyDataService.getTopByGain(3);
        List<DailyData> top3New = dailyDataService.getTopNew(3);
        List<DailyData> top3Volume = dailyDataService.getTopByVolume(3);

        //TODO: Refactor this
        topMarketCap.getContent().forEach(coin -> {
            double monthlyChange = dailyDataService.getMonthlyChange(coin.getSymbol());
            MarketData threeMonthsBefore = marketDataService.getKDaysDataOfSymbol(coin.getSymbol(), 90).getLast();
            double threeMonthsChange = dailyDataService.getChangeFromMarketData(coin.getSymbol(), threeMonthsBefore);
            coin.setMonthlyChange(monthlyChange);
            coin.setThreeMonthsChange(threeMonthsChange);
        });

        Map<String, List<Double>> sparklineData = new HashMap<>();
        for (DailyData coin : topMarketCap) {
            List<MarketData> weekly = marketDataService.getKDaysDataOfSymbol(coin.getSymbol(), 7);
            List<Double> res = weekly.stream().map(MarketData::getOpen).toList();
            sparklineData.put(coin.getSymbol(), res);
        }

        User u=(User)httpSession.getAttribute("user");
        model.addAttribute("user",u);

        List<String> symbols = coinService.getAllSymbols();
        model.addAttribute("symbols",symbols);
        model.addAttribute("totalNumberOfPages", topMarketCap.getTotalPages());

        model.addAttribute("pageNum", pageNum);
        model.addAttribute("top3Price", top3Price);
        model.addAttribute("top3Volume", top3Volume);
        model.addAttribute("top3New", top3New);
        model.addAttribute("top3Gain", top3Gain);
        model.addAttribute("tableCoins", topMarketCap);
        model.addAttribute("sparklineData", sparklineData);
        return "master-template";
    }
}
