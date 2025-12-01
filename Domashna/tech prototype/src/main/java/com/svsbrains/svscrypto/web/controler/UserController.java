package com.svsbrains.svscrypto.web.controler;

import com.svsbrains.svscrypto.model.DailyData;
import com.svsbrains.svscrypto.model.MarketData;
import com.svsbrains.svscrypto.model.User;
import com.svsbrains.svscrypto.service.DailyDataService;
import com.svsbrains.svscrypto.service.MarketDataService;
import com.svsbrains.svscrypto.service.UserService;
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

    public UserController(UserService userService, HttpSession httpSession,DailyDataService dailyDataService,MarketDataService marketDataService) {
        this.userService = userService;
        this.httpSession = httpSession;
        this.dailyDataService=dailyDataService;
        this.marketDataService=marketDataService;
    }

    @GetMapping("/login")
    public String logIn(Model model){
        model.addAttribute("bodyContent","log-in-form");
        return "master-template";
    }
    @PostMapping("/login")
    public String logInUser(@RequestParam String username,@RequestParam String password,Model model){
        User user=userService.logInUserByUsername(username,password);
        if(user==null) return "redirect:/login?error";
        httpSession.setAttribute("user",user);
        return "redirect:/dashboard";
    }
    @GetMapping("/signin")
    public String signIn(Model model){
        model.addAttribute("bodyContent","sign-in-form");
        return "master-template";
    }
    @PostMapping("/signin")
    public String signInUser(@RequestParam String firstname,@RequestParam String lastname,
                             @RequestParam String email,@RequestParam String username,
                             @RequestParam String password, Model model){
        userService.signInUser(username,firstname,lastname,email,password);
        return "redirect:/login";
    }
    @GetMapping("/watchlist")
    public String getWatchList(Model model){
        model.addAttribute("bodyContent","user-list");

        User u=(User)httpSession.getAttribute("user");
        List<DailyData> topPrice = u.getCoins().stream().map(symbol->dailyDataService.getBySymbol(symbol).orElse(null)).toList();


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

        model.addAttribute("user",httpSession.getAttribute("user"));
        model.addAttribute("tableCoins", topPrice);
        model.addAttribute("sparklineData", sparklineData);
        return "master-template";
    }
}
