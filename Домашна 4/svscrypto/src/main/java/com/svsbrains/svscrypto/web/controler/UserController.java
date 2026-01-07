package com.svsbrains.svscrypto.web.controler;

import com.svsbrains.svscrypto.model.DailyData;
import com.svsbrains.svscrypto.model.User;
import com.svsbrains.svscrypto.service.CoinService;
import com.svsbrains.svscrypto.service.DailyDataService;
import com.svsbrains.svscrypto.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Controller for handling user-related operations.
 * <p>
 * This controller manages authentication views (login page) and user-specific
 * functionalities such as managing the watchlist of cryptocurrencies.
 * </p>
 */
@Controller
public class UserController {
    private final UserService userService;
    private final DailyDataService dailyDataService;
    private final CoinService coinService;

    public UserController(UserService userService, DailyDataService dailyDataService, CoinService coinService) {
        this.userService = userService;
        this.dailyDataService = dailyDataService;
        this.coinService = coinService;
    }

    @GetMapping("/login")
    public String logIn(Model model) {
        model.addAttribute("symbols", dailyDataService.getCoinsBySymbols(coinService.getAllSymbols()));
        model.addAttribute("bodyContent", "log-in-form");
        model.addAttribute("pageTitle", "Log in");
        return "master-template";
    }

    @GetMapping("/watchlist")
    public String getWatchList(Model model,
                               @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("bodyContent", "user-list");

        User user = userService.getCurrentUser(userDetails);

        List<DailyData> topPrice = user.getCoins().stream().map(dailyDataService::getBySymbol).toList();
        dailyDataService.setCoinsChanges(topPrice);

        Map<String, List<Double>> sparklineData = dailyDataService.getSparklineData(topPrice);

        model.addAttribute("symbols", dailyDataService.getCoinsBySymbols(coinService.getAllSymbols()));
        model.addAttribute("user", user);
        model.addAttribute("tableCoins", topPrice);
        model.addAttribute("sparklineData", sparklineData);
        model.addAttribute("pageTitle", "My Watchlist");
        return "master-template";
    }

    @PostMapping("/add-coin-to-watchlist")
    public String addCoinToWatchlist(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String symbol, HttpServletRequest request) {
        userService.addCoinToWatchlist(userDetails.getUsername(), symbol);
        return safeRedirectBackHelper(request);
    }

    @PostMapping("/delete-coin-from-watchlist")
    public String deleteCoinFromWatchlist(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String symbol, HttpServletRequest request) {
        userService.removeCoinFromWatchlist(userDetails.getUsername(), symbol);
        return safeRedirectBackHelper(request);
    }

    /**
     * Helper method for safely redirecting the user back to the referring page.
     * Falls back to "/dashboard" if no referer is available.
     *
     * @param request the HTTP request containing the referer header
     * @return a redirect string for Spring MVC
     */
    private String safeRedirectBackHelper(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/dashboard");
    }
}
