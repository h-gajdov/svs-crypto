package com.svsbrains.svscrypto.web.controler;

import com.svsbrains.svscrypto.model.dto.SymbolIndicators;
import com.svsbrains.svscrypto.service.IndicatorsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
public class IndicatorController {

    private final IndicatorsService service;

    public IndicatorController(IndicatorsService service) {
        this.service = service;
    }

    @GetMapping("/api/indicators")
    public SymbolIndicators getIndicators(
            @RequestParam String symbol
    ) {
        return service.getIndicators(symbol);
    }
}
