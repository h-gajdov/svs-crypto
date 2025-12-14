package com.svsbrains.svscrypto.web.controler;

import com.svsbrains.svscrypto.model.dto.PredictionResponseDto;
import com.svsbrains.svscrypto.service.PredictionService;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prediction")
public class PricePredictionController {
    private final PredictionService predictionService;

    public PricePredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @GetMapping
    public ResponseEntity<PredictionResponseDto> getPrediction(@RequestParam String symbol) {
        PredictionResponseDto prediction = predictionService.predictPrice(symbol);

        if (prediction != null) {
            return ResponseEntity.ok(prediction);
        } else {
            return ResponseEntity.status(503).build();
        }
    }
}
