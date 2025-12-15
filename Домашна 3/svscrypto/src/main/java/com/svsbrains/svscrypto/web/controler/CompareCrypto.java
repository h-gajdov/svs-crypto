package com.svsbrains.svscrypto.web.controler;

import com.svsbrains.svscrypto.model.MarketData;
import com.svsbrains.svscrypto.service.CoinService;
import com.svsbrains.svscrypto.service.MarketDataService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/compare-coins")
@SessionAttributes("currentSymbols")
public class CompareCrypto {

    private final CoinService coinService;
    private final MarketDataService marketDataService;

    public CompareCrypto(CoinService coinService, MarketDataService marketDataService) {
        this.coinService = coinService;
        this.marketDataService = marketDataService;
    }

    @GetMapping
    public String getCompareCrypto(Model model,
                                   @SessionAttribute(name = "currentSymbols", required = false) List<String> currentSymbols,
                                   @RequestParam(required = false) String error,
                                   @RequestParam(defaultValue = "30", required = false) Integer days) {
        if (currentSymbols == null) {
            currentSymbols = new ArrayList<>();
        }
        if (error != null) {
            model.addAttribute("error", error);
        }

        model.addAttribute("currentSymbols", currentSymbols);
        Long timestamp = getTimestamp(days);
        List<List<MarketData>> marketData = currentSymbols.stream().map(marketDataService::getBySymbol)
                .map(l -> l.stream()
                        .filter(m -> m.getTimestamp() >= timestamp).toList())
                .collect(Collectors.toList());

        List<String> symbols = coinService.getAllSymbols();
        symbols.removeAll(currentSymbols);

        model.addAttribute("days", days);
        model.addAttribute("marketData", marketData);
        model.addAttribute("symbols", symbols);
        model.addAttribute("bodyContent", "compare-crypto");
        model.addAttribute("pageTitle", "Compare Coins");
        return "master-template";
    }

    private Long getTimestamp(Integer days) {
        return LocalDate.now().minusDays(days).atStartOfDay(ZoneOffset.UTC).toEpochSecond();
    }

    @PostMapping
    public String addCompareCoin(@RequestParam String symbol,
                                 @SessionAttribute("currentSymbols") List<String> currentSymbols) {

        if (currentSymbols.size() >= 5) {
            return "redirect:/compare-coins?error=Compare%20coin%20limit%20reached";
        }

        currentSymbols.add(symbol);
        return "redirect:/compare-coins";
    }

    @PostMapping("/remove")
    public String removeCompareCoin(@RequestParam String symbol,
                                    @SessionAttribute("currentSymbols") List<String> currentSymbols) {
        if (!currentSymbols.contains(symbol)) {
            return "redirect:/compare-coins?error=Cannot%20remove%20coin";
        }
        currentSymbols.remove(symbol);
        return "redirect:/compare-coins";
    }
}