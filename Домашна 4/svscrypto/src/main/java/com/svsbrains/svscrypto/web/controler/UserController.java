package com.svsbrains.svscrypto.web.controler;

import com.svsbrains.svscrypto.model.DailyData;
import com.svsbrains.svscrypto.model.MarketData;
import com.svsbrains.svscrypto.model.User;
import com.svsbrains.svscrypto.service.DailyDataService;
import com.svsbrains.svscrypto.service.MarketDataService;
import com.svsbrains.svscrypto.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {
    private final UserService userService;
    private final DailyDataService dailyDataService;
    private final MarketDataService marketDataService;

    public UserController(UserService userService, DailyDataService dailyDataService, MarketDataService marketDataService) {
        this.userService = userService;
        this.dailyDataService = dailyDataService;
        this.marketDataService = marketDataService;
    }

    @GetMapping("/login")
    public String logIn(Model model) {
        model.addAttribute("bodyContent", "log-in-form");
        model.addAttribute("pageTitle", "Log in");
        return "master-template";
    }

    @GetMapping("/watchlist")
    public String getWatchList(Model model,
                               @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("bodyContent", "user-list");

        User user = userService.findByUsername(userDetails.getUsername());

        List<DailyData> topPrice = user.getCoins().stream().map(dailyDataService::getBySymbol).toList();
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
        model.addAttribute("pageTitle", "My Watchlist");
        return "master-template";
    }

    @PostMapping("/add-coin-to-watchlist")
    public String addCoinToWatchlist(@RequestParam String username, @RequestParam String symbol, HttpSession httpSession, HttpServletRequest request) {
        User u = userService.addCoinToWatchlist(username, symbol);
        httpSession.setAttribute("user", u);
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/dashboard");
    }

    @PostMapping("/delete-coin-from-watchlist")
    public String deleteCoinFromWatchlist(@RequestParam String username, @RequestParam String symbol, HttpSession httpSession, HttpServletRequest request) {
        User u = userService.removeCoinFromWatchlist(username, symbol);
        httpSession.setAttribute("user", u);
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/dashboard");
    }
}
