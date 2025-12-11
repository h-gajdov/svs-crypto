package com.svsbrains.svscrypto.service.impl;

import com.svsbrains.svscrypto.model.dto.PredictionResponseDto;
import com.svsbrains.svscrypto.service.PredictionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PythonLSTMPredictionService implements PredictionService {
    private final RestTemplate restTemplate;

    private final String PYTHON_API_URL = "http://localhost:8000/api/predict";

    public PythonLSTMPredictionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    @Override
    public PredictionResponseDto predictPrice(String symbol) {
        try {
            String url = String.format("%s?symbol=%s", PYTHON_API_URL, symbol);

            ResponseEntity<PredictionResponseDto> response =
                    restTemplate.getForEntity(url, PredictionResponseDto.class);

            return response.getBody();

        } catch (Exception e) {
            System.err.println("Communication error with python: " + e.getMessage());
            return null;
        }
    }
}
