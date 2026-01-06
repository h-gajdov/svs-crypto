package com.svsbrains.svscrypto.web.controler;

import com.svsbrains.svscrypto.model.DailyData;
import com.svsbrains.svscrypto.model.User;
import com.svsbrains.svscrypto.service.CoinService;
import com.svsbrains.svscrypto.service.DashboardService;
import com.svsbrains.svscrypto.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DashboardController {

    private static final int PAGE_SIZE = 10;

    private final DashboardService dashboardService;
    private final CoinService coinService;
    private final UserService userService;

    public DashboardController(DashboardService dashboardService,
                               CoinService coinService, UserService userService) {
        this.dashboardService = dashboardService;
        this.coinService = coinService;
        this.userService = userService;
    }

    @GetMapping({"/", "/dashboard"})
    public String showDashboard(Model model,
                                @AuthenticationPrincipal UserDetails userDetails,
                                @RequestParam(defaultValue = "1") int pageNum) {

        Page<DailyData> topMarketCap = dashboardService.getDashboardMarketCap(pageNum - 1, PAGE_SIZE);

        User user = null;
        if(userDetails != null) {
            user = userService.findByUsername(userDetails.getUsername());
        }
        model.addAttribute("user",user);

        model.addAttribute("symbols", coinService.getAllSymbols());

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
