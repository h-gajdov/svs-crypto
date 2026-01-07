package com.svsbrains.svscrypto.web.controler;

import com.svsbrains.svscrypto.model.User;
import com.svsbrains.svscrypto.service.CoinService;
import com.svsbrains.svscrypto.service.DailyDataService;
import com.svsbrains.svscrypto.service.DetailedCoinViewService;
import com.svsbrains.svscrypto.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller responsible for displaying detailed information about a specific cryptocurrency.
 * <p>
 * This controller handles both the detailed coin view page and retrieving plot data for charts.
 * It supports tracking search history for authenticated users.
 * </p>
 */
@Controller
@RequestMapping("/details")
public class DetailedViewController {

    private final DetailedCoinViewService detailedCoinViewService;
    private final CoinService coinService;
    private final UserService userService;
    private final DailyDataService dailyDataService;

    public DetailedViewController(DetailedCoinViewService detailedCoinViewService, CoinService coinService, UserService userService, DailyDataService dailyDataService) {
        this.detailedCoinViewService = detailedCoinViewService;
        this.coinService=coinService;
        this.userService = userService;
        this.dailyDataService = dailyDataService;
    }

    /**
     * Handles GET requests to display the detailed view of a specific cryptocurrency.
     * <p>
     * Prepares the model with:
     * - all detailed coin data from {@link DetailedCoinViewService},
     * - currently authenticated user information (if logged in),
     * - updates the user's search history,
     * - list of all available symbols,
     * - the page title for the template.
     * </p>
     *
     * @param symbol      the cryptocurrency symbol to display (e.g., "BTC")
     * @param userDetails the currently authenticated user, if any
     * @param model       the model used to pass attributes to the Thymeleaf template
     * @return the name of the Thymeleaf template to render ("master-template")
     */
    @GetMapping("/{symbol}")
    public String showDetails(@PathVariable String symbol,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model) {

        model.addAllAttributes(detailedCoinViewService.buildDetailedView(symbol));
        if(userDetails != null) {
            User user = userService.getCurrentUser(userDetails);
            model.addAttribute("user",user);
            userService.addCoinToSearchHistory(user.getUsername(), symbol);
        }

        List<String> symbols = coinService.getAllSymbols();
        model.addAttribute("symbols", dailyDataService.getCoinsBySymbols(symbols));
        model.addAttribute("pageTitle", symbol);
        return "master-template";
    }

    /**
     * Handles GET requests to retrieve plot data for a specific cryptocurrency over a given timeframe.
     *
     * @param symbol the cryptocurrency symbol (e.g., "BTC")
     * @param time   the timeframe for the plot (e.g., "1D", "1W", "1M")
     * @param field  the field to plot (default: "open")
     * @return a map containing the plot data (timestamps and values)
     */
    @GetMapping("/{symbol}/plot/{time}")
    @ResponseBody
    public Map<String, Object> getPlotData(@PathVariable String symbol, @PathVariable String time, @RequestParam(defaultValue = "open") String field) {
        return detailedCoinViewService.getPlotData(symbol, time, field);
    }
}
