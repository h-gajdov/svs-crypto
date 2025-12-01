package com.svsbrains.svscrypto.web.controler;

import com.svsbrains.svscrypto.model.DailyData;
import com.svsbrains.svscrypto.service.DailyDataService;
import com.svsbrains.svscrypto.service.MarketDataService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

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
        List<DailyData> top3Price = dailyDataService.getTopByPrice(3);
        List<DailyData> top3Gain = dailyDataService.getTopByGain(3);
        List<DailyData> top3New = dailyDataService.getTopNew(3);
        List<DailyData> top3Volume = dailyDataService.getTopByVolume(3);
        model.addAttribute("top3Price", top3Price);
        model.addAttribute("top3Volume", top3Volume);
        model.addAttribute("top3New", top3New);
        model.addAttribute("top3Gain", top3Gain);
        return "master-template";
    }
}
