package com.svsbrains.svscrypto.web.controler;

import com.svsbrains.svscrypto.model.User;
import com.svsbrains.svscrypto.service.CoinService;
import com.svsbrains.svscrypto.service.DetailedCoinViewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/details")
public class DetailedViewController {

    private final DetailedCoinViewService detailedCoinViewService;
    private final CoinService coinService;

    public DetailedViewController(DetailedCoinViewService detailedCoinViewService, CoinService coinService) {
        this.detailedCoinViewService = detailedCoinViewService;
        this.coinService=coinService;
    }

    @GetMapping("/{symbol}")
    public String showDetails(@PathVariable String symbol, Model model, HttpSession httpSession) {

        model.addAllAttributes(detailedCoinViewService.buildDetailedView(symbol));

        User user=(User)httpSession.getAttribute("user");
        model.addAttribute("user",user);

        if(user!=null){
            user.addHistoryCoin(symbol);
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
