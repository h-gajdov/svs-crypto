package com.svsbrains.svscrypto.web.controler;

import com.svsbrains.svscrypto.service.CoinService;
import com.svsbrains.svscrypto.service.MarketDataService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller responsible for handling coin comparison functionality.
 * <p>
 * This controller allows users to add cryptocurrency symbols to a comparison list,
 * remove symbols, and view market data for selected coins over a specified timeframe.
 * The comparison list is stored in the user's session via the "currentSymbols" attribute.
 * </p>
 */
@Controller
@RequestMapping("/compare-coins")
@SessionAttributes("currentSymbols")
public class CompareCryptoController {

    private static final int MAX_COMPARE_COINS=5;

    private final CoinService coinService;
    private final MarketDataService marketDataService;

    public CompareCryptoController(CoinService coinService, MarketDataService marketDataService) {
        this.coinService = coinService;
        this.marketDataService = marketDataService;
    }

    /**
     * Initializes the "currentSymbols" session attribute.
     * <p>
     * This list stores the symbols currently selected for comparison.
     *
     * @return an empty list of symbols
     */
    @ModelAttribute("currentSymbols")
    public List<String> initCurrentSymbols() {
        return new ArrayList<>();
    }

    /**
     * Handles GET requests to the comparison page.
     * <p>
     * Prepares the model with:
     * - the current list of selected symbols,
     * - the list of all available symbols not already selected,
     * - market data for the selected symbols,
     * - the selected timeframe for historical data,
     * - template and page title information.
     *
     * @param model           the model used by Thymeleaf
     * @param currentSymbols  the list of symbols currently in the comparison list
     * @param error           optional error message to display
     * @param days            number of days for market data history (default: 30)
     * @return the name of the Thymeleaf template to render ("master-template")
     */
    @GetMapping
    public String getCompareCrypto(Model model,
                                   @ModelAttribute("currentSymbols") List<String> currentSymbols,
                                   @RequestParam(required = false) String error,
                                   @RequestParam(defaultValue = "30", required = false) Integer days) {

        if (error != null) {
            model.addAttribute("error", error);
        }

        List<String> symbols = coinService.getAllSymbols();
        symbols.removeAll(currentSymbols);

        model.addAttribute("days", days);
        model.addAttribute("marketData", marketDataService.getMarketDataForSymbols(currentSymbols, days));
        model.addAttribute("symbols",symbols);
        model.addAttribute("bodyContent", "compare-crypto");
        model.addAttribute("pageTitle", "Compare Coins");

        return "master-template";
    }

    @PostMapping
    public String addCompareCoin(@RequestParam String symbol,
                                 @SessionAttribute("currentSymbols") List<String> currentSymbols) {

        if (currentSymbols.size() >= MAX_COMPARE_COINS) {
            return redirectWithError("You can only compare up to 5 coins");
        }

        currentSymbols.add(symbol);
        return "redirect:/compare-coins";
    }

    @PostMapping("/remove")
    public String removeCompareCoin(@RequestParam String symbol,
                                    @SessionAttribute("currentSymbols") List<String> currentSymbols) {
        if (!currentSymbols.contains(symbol)) {
            return redirectWithError("Cannot remove coin");
        }
        currentSymbols.remove(symbol);
        return "redirect:/compare-coins";
    }

    /**
     * Helper method to redirect to the comparison page with an error message.
     * <p>
     * Spaces in the message are replaced with "%20" for URL encoding.
     *
     * @param message the error message to display
     * @return redirect URL containing the error query parameter
     */
    private String redirectWithError(String message) {
        return "redirect:/compare-coins?error=" + message.replace(" ", "%20");
    }
}