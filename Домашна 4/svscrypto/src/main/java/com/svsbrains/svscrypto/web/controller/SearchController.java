package com.svsbrains.svscrypto.web.controller;

import com.svsbrains.svscrypto.model.DailyData;
import com.svsbrains.svscrypto.service.DailyDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final DailyDataService dailyDataService;

    public SearchController(DailyDataService dailyDataService) {
        this.dailyDataService = dailyDataService;
    }

    @GetMapping("/symbols")
    public List<DailyData> searchSymbols(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return dailyDataService
                .searchBySymbolOrName(q, limit)
                .getContent();
    }
}