package com.svsbrains.svscrypto.web.controller;

import com.svsbrains.svscrypto.model.dto.SymbolAnalysisResponse;
import com.svsbrains.svscrypto.service.TechnicalAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static com.svsbrains.svscrypto.util.ResponseUtils.respondOrServiceUnavailable;

/**
 * REST controller for accessing technical analysis data for cryptocurrencies.
 * <p>
 * This controller provides an endpoint to retrieve technical indicators and trading signals
 * for a given cryptocurrency symbol. The response is wrapped in a {@link ResponseEntity},
 * and if the service is unavailable or the data is missing, a 503 Service Unavailable response
 * is returned using {@link com.svsbrains.svscrypto.util.ResponseUtils#respondOrServiceUnavailable(Object)}.
 * </p>
 */
@Controller
public class TechnicalAnalysisController {

    private final TechnicalAnalysisService technicalAnalysisService;

    public TechnicalAnalysisController(TechnicalAnalysisService technicalAnalysisService) {
        this.technicalAnalysisService = technicalAnalysisService;
    }

    @GetMapping("/technical-analysis")
    public ResponseEntity<SymbolAnalysisResponse> getTechnicalAnalysis(@RequestParam String symbol) {
        return respondOrServiceUnavailable(technicalAnalysisService.getTechnicalAnalysis(symbol));
    }
}
