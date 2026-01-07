package com.svsbrains.svscrypto.web.controller;

import com.svsbrains.svscrypto.model.DailyData;
import com.svsbrains.svscrypto.model.User;
import com.svsbrains.svscrypto.service.CoinService;
import com.svsbrains.svscrypto.service.DailyDataService;
import com.svsbrains.svscrypto.service.DashboardService;
import com.svsbrains.svscrypto.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controller responsible for handling the dashboard page.
 * <p>
 * This controller displays the main dashboard of the cryptocurrency application,
 * including market data, top-performing coins, recent additions, and user-specific information.
 * Pagination is supported for the main market cap table.
 * </p>
 */
@Controller
public class DashboardController {
    private final DashboardService dashboardService;
    private final UserService userService;
    private final CoinService coinService;
    private final DailyDataService dailyDataService;

    public DashboardController(DashboardService dashboardService,
                               UserService userService, CoinService coinService, DailyDataService dailyDataService) {
        this.dashboardService = dashboardService;
        this.userService = userService;
        this.coinService = coinService;
        this.dailyDataService = dailyDataService;
    }

    @GetMapping({"/", "/dashboard"})
    public String showDashboardPage(Model model,
                                @AuthenticationPrincipal UserDetails userDetails,
                                @RequestParam(defaultValue = "1") int pageNum,
                                @RequestParam(defaultValue = "10") int pageSize) {

        Page<DailyData> topMarketCap = dashboardService.getDashboardMarketCap(pageNum - 1, pageSize);

        User user = userService.getCurrentUser(userDetails);
        model.addAttribute("user",user);

        List<String> symbols = coinService.getAllSymbols();
        model.addAttribute("symbols", dailyDataService.getCoinsBySymbols(symbols));

        model.addAttribute("pageNum", pageNum);
        model.addAttribute("totalNumberOfPages", topMarketCap.getTotalPages());

        model.addAttribute("top3Price", dashboardService.getTopByPrice());
        model.addAttribute("top3Volume", dashboardService.getTopByVolume());
        model.addAttribute("top3New", dashboardService.getTopNew());
        model.addAttribute("top3Gain", dashboardService.getTopByGain());

        model.addAttribute("tableCoins", topMarketCap);
        model.addAttribute("sparklineData", dashboardService.getSparklineData(topMarketCap));

        model.addAttribute("bodyContent", "dashboard");
        model.addAttribute("pageTitle", "Dashboard");

        return "master-template";
    }
}
