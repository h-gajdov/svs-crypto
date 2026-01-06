package com.svsbrains.svscrypto.web.controler;

import com.svsbrains.svscrypto.model.User;
import com.svsbrains.svscrypto.service.CoinService;
import com.svsbrains.svscrypto.service.DetailedCoinViewService;
import com.svsbrains.svscrypto.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/details")
public class DetailedViewController {

    private final DetailedCoinViewService detailedCoinViewService;
    private final CoinService coinService;
    private final UserService userService;

    public DetailedViewController(DetailedCoinViewService detailedCoinViewService, CoinService coinService, UserService userService) {
        this.detailedCoinViewService = detailedCoinViewService;
        this.coinService=coinService;
        this.userService = userService;
    }

    @GetMapping("/{symbol}")
    public String showDetails(@PathVariable String symbol,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model) {

        model.addAllAttributes(detailedCoinViewService.buildDetailedView(symbol));
        if(userDetails != null) {
            User user = userService.findByUsername(userDetails.getUsername());
            model.addAttribute("user",user);
            userService.addCoinToSearchHistory(user.getUsername(), symbol);
        }

        model.addAttribute("symbols",coinService.getAllSymbols());
        model.addAttribute("pageTitle", symbol);
        return "master-template";
    }

    @GetMapping("/{symbol}/plot/{time}")
    @ResponseBody
    public Map<String, Object> getPlotData(@PathVariable String symbol, @PathVariable String time, @RequestParam(defaultValue = "open") String field) {
        return detailedCoinViewService.getPlotData(symbol, time, field);
    }
}
