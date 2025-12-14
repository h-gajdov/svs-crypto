package com.svsbrains.svscrypto.web.controler;

import com.svsbrains.svscrypto.model.dto.SymbolAnalysisResponse;
import com.svsbrains.svscrypto.service.TechnicalAnalysisService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Controller
public class TechnicalAnalysisController {

    private final TechnicalAnalysisService service;

    public TechnicalAnalysisController(TechnicalAnalysisService service) {
        this.service = service;
    }

    @GetMapping("/technical-analysis")
    @ResponseBody
    public Map<String, Object> getTA(@RequestParam String symbol) {
        return service.getTechnicalAnalysis(symbol);
    }

    /*
    @PostMapping("/analyze")
    public String analyzeSymbol(@RequestParam String symbol, Model model) {
        SymbolAnalysisResponse result = service.analyzeSymbol(symbol);
        model.addAttribute("result", result);
        return "technical-analysis";
    }

    @GetMapping("/analyze-all")
    public String analyzeAll(Model model) {
        Map<String, Object> results = service.analyzeAll();
        model.addAttribute("allResults", results);
        return "technical-analysis";
    }*/
}
