package com.svsbrains.svscrypto.web.controler;

import com.svsbrains.svscrypto.model.dto.SymbolIndicatorsDto;
import com.svsbrains.svscrypto.service.IndicatorsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class IndicatorController {

    private final IndicatorsService indicatorsService;

    public IndicatorController(IndicatorsService indicatorsService) {
        this.indicatorsService = indicatorsService;
    }

    @GetMapping("/api/indicators")
    public ResponseEntity<SymbolIndicatorsDto> getIndicators(
            @RequestParam String symbol
    ) {
        SymbolIndicatorsDto data = indicatorsService.getIndicators(symbol);

        if (data != null) {
            return ResponseEntity.ok(data);
        } else {
            return ResponseEntity.status(503).build();
        }
    }
}
