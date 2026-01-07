package com.svsbrains.svscrypto.web.controler;

import com.svsbrains.svscrypto.model.DailyData;
import com.svsbrains.svscrypto.model.User;
import com.svsbrains.svscrypto.service.CoinService;
import com.svsbrains.svscrypto.service.DailyDataService;
import com.svsbrains.svscrypto.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * Controller responsible for displaying the historical cryptocurrency data
 * of a logged-in user.
 * <p>
 * This controller handles requests to the "/historical-data" endpoint,
 * retrieves the user's search history, calculates coin performance metrics
 * (monthly change, 3-month change), generates weekly sparkline data for charts,
 * and prepares the model for rendering the history page.
 * </p>
 */
@Controller
@RequestMapping("/historical-data")
public class HistoricalDataController {
    private final DailyDataService dailyDataService;
    private final UserService userService;
    private final CoinService coinService;

    public HistoricalDataController(DailyDataService dailyDataService, UserService userService, CoinService coinService) {
        this.dailyDataService = dailyDataService;
        this.userService = userService;
        this.coinService = coinService;
    }

    @GetMapping
    public String getHistory(Model model,
                             @AuthenticationPrincipal UserDetails userDetails){
        User user = userService.getCurrentUser(userDetails);
        model.addAttribute("bodyContent","history");

        List<DailyData> topPrice = userService.getSearchHistory(user.getUsername());
        dailyDataService.setCoinsChanges(topPrice);

        Map<String, List<Double>> sparklineData = dailyDataService.getSparklineData(topPrice);

        List<String> symbols = coinService.getAllSymbols();
        model.addAttribute("symbols", dailyDataService.getCoinsBySymbols(symbols));
        model.addAttribute("user", user);
        model.addAttribute("tableCoins", topPrice);
        model.addAttribute("sparklineData", sparklineData);
        model.addAttribute("pageTitle", "Search history");
        return "master-template";
    }
}
