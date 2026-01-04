package com.svsbrains.svscrypto.web.controler;

import com.svsbrains.svscrypto.service.CoinService;
import com.svsbrains.svscrypto.service.MarketDataService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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

    @ModelAttribute("currentSymbols")
    public List<String> initCurrentSymbols() {
        return new ArrayList<>();
    }

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
            return redirectWithError("Compare coin limit reached");
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

    private String redirectWithError(String message) {
        return "redirect:/compare-coins?error=" + message.replace(" ", "%20");
    }
}