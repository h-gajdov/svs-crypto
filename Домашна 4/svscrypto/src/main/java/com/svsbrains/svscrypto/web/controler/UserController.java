package com.svsbrains.svscrypto.web.controler;

import com.svsbrains.svscrypto.model.DailyData;
import com.svsbrains.svscrypto.model.MarketData;
import com.svsbrains.svscrypto.model.User;
import com.svsbrains.svscrypto.service.CoinService;
import com.svsbrains.svscrypto.service.DailyDataService;
import com.svsbrains.svscrypto.service.MarketDataService;
import com.svsbrains.svscrypto.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.boot.Banner;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {
    private final UserService userService;
    private final HttpSession httpSession;
    private final DailyDataService dailyDataService;
    private final MarketDataService marketDataService;
    private final CoinService coinService;

    public UserController(UserService userService, HttpSession httpSession, DailyDataService dailyDataService, MarketDataService marketDataService, CoinService coinService) {
        this.userService = userService;
        this.httpSession = httpSession;
        this.dailyDataService = dailyDataService;
        this.marketDataService = marketDataService;
        this.coinService = coinService;
    }

    @GetMapping("/login")
    public String logIn(Model model) {
        model.addAttribute("bodyContent", "log-in-form");
        model.addAttribute("pageTitle", "Log in");
        return "master-template";
    }

    @PostMapping("/login")
    public String logInUser(@RequestParam String username, @RequestParam String password, Model model) {
        User user = userService.logInUserByUsername(username, password);
        if (user == null) return "redirect:/login?error";
        if (!user.isEnabled()) {
            userService.sendVerificationMail(user);
            return "redirect:/verify";
        }
        httpSession.setAttribute("user", user);
        return "redirect:/dashboard";
    }

    @GetMapping("/watchlist")
    public String getWatchList(Model model) {
        model.addAttribute("bodyContent", "user-list");

        User u = (User) httpSession.getAttribute("user");

        if (u == null) return "redirect:/login";

        List<DailyData> topPrice = u.getCoins().stream().map(symbol -> dailyDataService.getBySymbol(symbol).orElse(null)).toList();


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
        model.addAttribute("symbols", symbols);

        model.addAttribute("user", (User) httpSession.getAttribute("user"));
        model.addAttribute("tableCoins", topPrice);
        model.addAttribute("sparklineData", sparklineData);
        model.addAttribute("pageTitle", "My Watchlist");
        return "master-template";
    }

    @PostMapping("/addCoinToUser")
    public String addCoin(@RequestParam String username, @RequestParam String symbol, HttpSession httpSession, HttpServletRequest request) {
        User u = userService.addCoinToList(username, symbol);
        httpSession.setAttribute("user", u);
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/dashboard");
    }

    @PostMapping("/deleteCoinFromUser")
    public String removeCoin(@RequestParam String username, @RequestParam String symbol, HttpSession httpSession, HttpServletRequest request) {
        User u = userService.deleteCoinFromList(username, symbol);
        httpSession.setAttribute("user", u);
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/dashboard");
    }
}
