package com.svsbrains.svscrypto.web.controller;

import com.svsbrains.svscrypto.model.dto.SymbolIndicatorsDto;
import com.svsbrains.svscrypto.service.IndicatorsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static com.svsbrains.svscrypto.util.ResponseUtils.respondOrServiceUnavailable;

/**
 * REST controller that provides cryptocurrency technical indicator data for a given symbol.
 * <p>
 * Responses are automatically wrapped using {@link com.svsbrains.svscrypto.util.ResponseUtils#respondOrServiceUnavailable(Object)},
 * which returns HTTP 200 OK with data if present, or HTTP 503 Service Unavailable if no data is available.
 * </p>
 */
@RestController
public class IndicatorController {

    private final IndicatorsService indicatorsService;

    public IndicatorController(IndicatorsService indicatorsService) {
        this.indicatorsService = indicatorsService;
    }

    /**
     * Retrieves technical indicators for the specified cryptocurrency symbol.
     *
     * @param symbol the symbol of the cryptocurrency (e.g., "BTC")
     * @return {@link ResponseEntity} containing {@link SymbolIndicatorsDto} with indicators,
     *         or HTTP 503 if the data is unavailable
     */
    @GetMapping("/api/indicators")
    public ResponseEntity<SymbolIndicatorsDto> getIndicators(
            @RequestParam String symbol
    ) {
        return respondOrServiceUnavailable(indicatorsService.getIndicators(symbol));
    }
}
