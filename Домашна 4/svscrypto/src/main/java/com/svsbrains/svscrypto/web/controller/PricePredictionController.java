package com.svsbrains.svscrypto.web.controller;

import com.svsbrains.svscrypto.model.dto.PredictionResponseDto;
import com.svsbrains.svscrypto.service.PredictionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.svsbrains.svscrypto.util.ResponseUtils.respondOrServiceUnavailable;

/**
 * REST controller that exposes endpoints for retrieving price predictions of cryptocurrencies.
 * <p>
 * This controller provides an endpoint to get the predicted price for a given cryptocurrency symbol.
 * The response is automatically wrapped using {@link com.svsbrains.svscrypto.util.ResponseUtils#respondOrServiceUnavailable(Object)},
 * which returns HTTP 200 OK if the prediction is available, or HTTP 503 Service Unavailable if it is not.
 * </p>
 * <p>
 * All endpoints are prefixed with <code>/api/prediction</code>.
 * </p>
 */
@RestController
@RequestMapping("/api/prediction")
public class PricePredictionController {
    private final PredictionService predictionService;

    public PricePredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @GetMapping
    public ResponseEntity<PredictionResponseDto> getPrediction(@RequestParam String symbol) {
        return respondOrServiceUnavailable(predictionService.predictPrice(symbol));
    }
}
